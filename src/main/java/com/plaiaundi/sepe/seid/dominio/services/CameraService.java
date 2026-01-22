package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.util.CameraValidator;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import com.plaiaundi.sepe.seid.dto.OpenDataCameraResponse;
import com.plaiaundi.sepe.seid.infrastructure.ApiTrafico;

import com.plaiaundi.sepe.seid.infrastructure.mappers.CameraMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CameraService {

    @Autowired 
    private ApiTrafico apiTrafico;

    @Autowired
    private CameraRepository cameraRepository;

    @Autowired
    private CameraMapper cameraMapper;

    @Autowired
    private CameraValidator cameraValidator;

    @Autowired
    @Qualifier("hilosCamaras")
    private Executor executor;

    private final RestClient restClient = RestClient.create();

    public List<Camera> getCameras() {
        return cameraRepository.findAllByEstado(Camera.Estado.ACTIVA);
    }

    @Cacheable("camerasAPI")
    @Retryable(maxRetries = 3)
    public List<Camera> syncAllCamerasFromAPI() {
        log.info("🚀 INICIO: Sincronización masiva de cámaras...");
        long startTotal = System.currentTimeMillis();

        // ---------------------------------------------------------
        // FASE 1: Obtención de páginas (I/O Intensivo - Descarga)
        // ---------------------------------------------------------

        // 1. Primera página
        log.info("📄 [Main Thread] Descargando página 1 (Síncrona)...");
        OpenDataCameraResponse primeraPagina = apiTrafico.listaCamaras();
        int totalPaginas = primeraPagina.totalPages();
        log.info("📚 Total páginas detectadas: {}", totalPaginas);

        // 2. Descarga paralela
        List<CompletableFuture<OpenDataCameraResponse>> futurasPaginas = IntStream
                .rangeClosed(2, totalPaginas)
                .mapToObj(pagina -> CompletableFuture.supplyAsync(() -> {
                    // LOG DE TRAZA: Verás saltar números de página desordenados aquí
                    log.info("⬇️ [Hilo: {}] Solicitando página {}", Thread.currentThread().getName(), pagina);
                    return apiTrafico.listaCamaras(pagina);
                }, executor))
                .toList();

        // 3. Aplanado
        List<OpenDataCamera> todosLosDtos = Stream.concat(
                        Stream.of(primeraPagina),
                        futurasPaginas.stream().map(CompletableFuture::join)
                )
                .map(OpenDataCameraResponse::cameras)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();

        log.info("📦 FASE 1 COMPLETADA. Total cámaras crudas descargadas: {}", todosLosDtos.size());

        // ---------------------------------------------------------
        // FASE 2: Validación y Mapeo (CPU + I/O mixto)
        // ---------------------------------------------------------

        log.info("⚡ INICIO FASE 2: Validando y Mapeando en paralelo...");

        List<CompletableFuture<Camera>> camarasValidadasFutures = todosLosDtos.stream()
                .map(dto -> CompletableFuture.supplyAsync(() -> {
                    String threadName = Thread.currentThread().getName();

                    // LOG DETALLADO: Verás múltiples hilos trabajando a la vez
                    // Nota: Usa debug si son muchas cámaras para no saturar, info para pruebas
                    log.info("🔍 [Hilo: {}] Verificando cámara ID: {}", threadName, dto.cameraId());

                    boolean esValida = cameraValidator.isValid(dto);

                    if (esValida) {
                        try {
                            log.debug("✅ [Hilo: {}] Cámara {} válida. Mapeando...", threadName, dto.cameraId());
                            return cameraMapper.toEntity(dto);
                        } catch (Exception e) { // Capturamos Exception general para ver errores de mapeo
                            log.error("❌ [Hilo: {}] Error mapeando cámara {}: {}", threadName, dto.cameraId(), e.getMessage());
                            return null;
                        }
                    } else {
                        log.debug("🗑️ [Hilo: {}] Cámara {} descartada (Invalid/404)", threadName, dto.cameraId());
                        return null;
                    }
                }, executor))
                .toList();

        // 5. Recolección
        List<Camera> camarasValidadas = camarasValidadasFutures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // --- FASE 3: PERSISTENCIA OPTIMIZADA ---

        // FASE 3: PERSISTENCIA OPTIMIZADA

        // 1. Sacamos los IDs que queremos guardar
        List<Integer> idsNuevos = camarasValidadas.stream()
                .map(Camera::getId)
                .toList();

        // 2. Preguntamos cuáles YA existen (Llamada corregida)
        // Esto devuelve, por ejemplo: [101, 105, 200]
        List<Integer> idsQueYaExisten = cameraRepository.findExistingIds(idsNuevos);

        // 3. Estrategia de Guardado (Upsert manual para evitar errores)
        // Separamos las cámaras en dos grupos para entender qué pasa (opcional, pero seguro)

        // A. Nuevas (INSERT)
        List<Camera> paraInsertar = camarasValidadas.stream()
                .filter(c -> !idsQueYaExisten.contains(c.getId()))
                .map(c -> {
                    c.setNew(true); // Si usas Persistable
                    return c;
                })
                .toList();

        // B. Existentes (UPDATE)
        List<Camera> paraActualizar = camarasValidadas.stream()
                .filter(c -> idsQueYaExisten.contains(c.getId()))
                .map(c -> {
                    c.setNew(false); // Si usas Persistable
                    return c;
                })
                .toList();

// 4. Guardamos todo
        if (!paraInsertar.isEmpty()) cameraRepository.saveAll(paraInsertar);
        if (!paraActualizar.isEmpty()) cameraRepository.saveAll(paraActualizar);

        return camarasValidadas;
    }

    /*
    public List<Camera> getCamerasInPosition(double longitud, double latitud, int radio) {
        return cameraRepository.findByLatitudAndLongitud(latitud, longitud, radio);
    }*/
}