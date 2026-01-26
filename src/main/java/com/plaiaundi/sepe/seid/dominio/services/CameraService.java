package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.dao.RecursoRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dominio.util.CameraValidator;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import com.plaiaundi.sepe.seid.dto.OpenDataCameraResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataSource;
import com.plaiaundi.sepe.seid.infrastructure.ApiTrafico;
import com.plaiaundi.sepe.seid.infrastructure.mappers.CameraMapper;
import com.plaiaundi.sepe.seid.infrastructure.mappers.RecursoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Slf4j
public class CameraService {

    @Autowired
    private ApiTrafico apiTrafico;
    @Autowired
    private CameraRepository cameraRepository;
    @Autowired
    private RecursoRepository recursoRepository;
    @Autowired
    private CameraMapper cameraMapper;
    @Autowired
    private RecursoMapper recursoMapper;
    @Autowired
    private CameraValidator cameraValidator;
    @Autowired
    @Qualifier("hilosCamaras")
    private Executor executor;

    public List<Camera> getCameras() {
        return cameraRepository.findAllByEstado(Camera.Estado.ACTIVA);
    }

    @Cacheable("camerasAPI")
    @Retryable(maxRetries = 3)
    @Transactional
    public List<Camera> syncAllCamerasFromAPI() {
        log.info("🚀 INICIO: Sincronización masiva de cámaras...");

        // --- FASE 1: OBTENCIÓN DE DATOS CRUDOS ---
        log.info("📄 [Main Thread] Descargando página 1 (Síncrona)...");
        OpenDataCameraResponse primeraPagina = apiTrafico.listaCamaras();
        int totalPaginas = primeraPagina.totalPages();
        log.info("📚 Total páginas detectadas: {}", totalPaginas);

        List<CompletableFuture<OpenDataCameraResponse>> futurasPaginas = IntStream
                .rangeClosed(2, totalPaginas)
                .mapToObj(pagina -> CompletableFuture.supplyAsync(() -> {
                    log.info("⬇️ [Hilo: {}] Solicitando página {}", Thread.currentThread().getName(), pagina);
                    return apiTrafico.listaCamaras(pagina);
                }, executor))
                .toList();

        List<OpenDataCamera> todosLosDtos = Stream.concat(
                        Stream.of(primeraPagina),
                        futurasPaginas.stream().map(CompletableFuture::join)
                )
                .map(OpenDataCameraResponse::cameras)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
        log.info("📦 FASE 1 COMPLETADA. Total cámaras crudas descargadas: {}", todosLosDtos.size());

        // --- FASE 2: PREPARACIÓN DE RECURSOS ---
        log.info("🛠️ INICIO FASE 2: Preparando y cacheadando recursos...");

        // 1. Obtener la LISTA de DTOs de recursos y CONVERTIRLA A UN MAPA por ID.
        Map<Integer, OpenDataSource> recursosDtoMap = apiTrafico.listaRecursos().stream()
                .collect(Collectors.toMap(OpenDataSource::id, Function.identity(), (existente, nuevo) -> existente));

        Set<Integer> idsRecursosNecesarios = todosLosDtos.stream()
                .map(OpenDataCamera::sourceId)
                .collect(Collectors.toSet());

        Map<Integer, Recurso> recursosExistentes = recursoRepository.findAllById(idsRecursosNecesarios).stream()
                .collect(Collectors.toMap(Recurso::getId, Function.identity()));
        log.info("🔍 Encontrados {} recursos existentes en la BD.", recursosExistentes.size());

        Map<Integer, Recurso> mapaDeRecursosFinal = new HashMap<>(recursosExistentes);
        for (Integer idNecesario : idsRecursosNecesarios) {
            if (!mapaDeRecursosFinal.containsKey(idNecesario)) {
                // Ahora la búsqueda en recursosDtoMap es por clave, no por índice.
                OpenDataSource dto = recursosDtoMap.get(idNecesario);
                if (dto != null) {
                    Recurso nuevoRecurso = recursoMapper.toEntity(dto);
                    mapaDeRecursosFinal.put(idNecesario, nuevoRecurso);
                }
            }
        }
        log.info("✅ FASE 2 COMPLETADA. Mapa de recursos final contiene {} entradas.", mapaDeRecursosFinal.size());


        // --- FASE 3: VALIDACIÓN Y MAPEO EN PARALELO ---
        log.info("⚡ INICIO FASE 3: Validando y Mapeando en paralelo...");
        List<CompletableFuture<Camera>> camarasValidadasFutures = todosLosDtos.stream()
                .map(dto -> CompletableFuture.supplyAsync(() -> {
                    if (cameraValidator.isValid(dto)) {
                        try {
                            Recurso recurso = mapaDeRecursosFinal.get(dto.sourceId());
                            if (recurso != null) {
                                return cameraMapper.toEntity(dto, recurso);
                            }
                        } catch (Exception e) {
                            log.error("❌ [Hilo: {}] Error mapeando cámara {}: {}", Thread.currentThread().getName(), dto.cameraId(), e.getMessage());
                        }
                    }
                    return null;
                }, executor))
                .toList();

        List<Camera> camarasValidadas = camarasValidadasFutures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // --- FASE 4: PERSISTENCIA ---
        log.info("💾 INICIO FASE 4: Persistiendo cámaras...");
        List<Integer> idsNuevos = camarasValidadas.stream().map(Camera::getId).toList();
        List<Integer> idsQueYaExisten = cameraRepository.findExistingIds(idsNuevos);

        List<Camera> paraInsertar = camarasValidadas.stream()
                .filter(c -> !idsQueYaExisten.contains(c.getId()))
                .peek(c -> c.setNew(true))
                .toList();

        List<Camera> paraActualizar = camarasValidadas.stream()
                .filter(c -> idsQueYaExisten.contains(c.getId()))
                .peek(c -> c.setNew(false))
                .toList();

        if (!paraInsertar.isEmpty()) {
            log.info("➕ Insertando {} cámaras nuevas.", paraInsertar.size());
            cameraRepository.saveAll(paraInsertar);
        }
        if (!paraActualizar.isEmpty()) {
            log.info("🔄 Actualizando {} cámaras existentes.", paraActualizar.size());
            cameraRepository.saveAll(paraActualizar);
        }

        log.info("🏁 FIN: Sincronización completada. Total cámaras procesadas: {}", camarasValidadas.size());
        return camarasValidadas;
    }
}
