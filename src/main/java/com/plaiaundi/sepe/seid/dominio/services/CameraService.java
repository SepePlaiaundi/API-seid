package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.dao.RecursoRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.model.Estado;
import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dominio.util.CameraValidator;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import com.plaiaundi.sepe.seid.dto.OpenDataCameraResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataSource;
import com.plaiaundi.sepe.seid.infrastructure.ApiTrafico;
import com.plaiaundi.sepe.seid.infrastructure.CoordinateNormalizer;
import com.plaiaundi.sepe.seid.infrastructure.mappers.CameraMapper;
import com.plaiaundi.sepe.seid.infrastructure.mappers.RecursoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class CameraService {

    private static final Logger log = LoggerFactory.getLogger(CameraService.class);

    private final ApiTrafico apiTrafico;
    private final CameraRepository cameraRepository;
    private final RecursoRepository recursoRepository;
    private final CameraMapper cameraMapper;
    private final RecursoMapper recursoMapper;
    private final CameraValidator cameraValidator;
    private final CoordinateNormalizer coordinateNormalizer;
    private final Executor executor;

    public CameraService(ApiTrafico apiTrafico,
            CameraRepository cameraRepository,
            RecursoRepository recursoRepository,
            CameraMapper cameraMapper,
            RecursoMapper recursoMapper,
            CameraValidator cameraValidator,
            CoordinateNormalizer coordinateNormalizer,
            @Qualifier("hilosCamaras") Executor executor) {
        this.apiTrafico = apiTrafico;
        this.cameraRepository = cameraRepository;
        this.recursoRepository = recursoRepository;
        this.cameraMapper = cameraMapper;
        this.recursoMapper = recursoMapper;
        this.cameraValidator = cameraValidator;
        this.coordinateNormalizer = coordinateNormalizer;
        this.executor = executor;
    }

    public List<Camera> getCameras() {
        return cameraRepository.findAllByEstado(Estado.ACTIVA);
    }

    public Optional<Camera> getById(Integer id) {
        return cameraRepository.findById(id);
    }

    public List<Camera> getCamerasByEstado(Estado estado) {
        return cameraRepository.findAllByEstado(estado);
    }

    public List<Camera> getAllCameras() {
        return cameraRepository.findAll();
    }

    public Camera save(Camera camera) {
        return cameraRepository.save(camera);
    }

    public void delete(Integer id) {
        cameraRepository.findById(id).ifPresent(opt -> {
            opt.setEstado(Estado.ELIMINADA);
            cameraRepository.save(opt);
        });
    }

    public Optional<Camera> toggleStatus(Integer id) {
        return cameraRepository.findById(id).map(camera -> {
            if (camera.getEstado() == Estado.ACTIVA) {
                camera.setEstado(Estado.INACTIVA);
            } else if (camera.getEstado() == Estado.INACTIVA) {
                camera.setEstado(Estado.ACTIVA);
            }
            return cameraRepository.save(camera);
        });
    }

    private List<OpenDataCamera> obtencionDeDatosCrudos() {
        // Descarga de pagina inicial para obtencion de metadata
        log.debug("📄 [Main Thread] Descargando página 1 (Síncrona)...");
        OpenDataCameraResponse primeraPagina = apiTrafico.listaCamaras();
        int totalPaginas = primeraPagina.totalPages();
        log.debug("📚 Total páginas detectadas: {}", totalPaginas);

        // Descarga paralela de paginas de opendata
        List<CompletableFuture<OpenDataCameraResponse>> futurasPaginas = IntStream
                .rangeClosed(2, totalPaginas) // Abrimos un Stream de 2 al total de paginas
                .mapToObj(pagina -> CompletableFuture.supplyAsync(() -> { // Mapeamos cada pagina como un objeto
                                                                          // completable
                    log.debug("⬇️ [Hilo: {}] Solicitando página {}", Thread.currentThread().getName(), pagina);
                    return apiTrafico.listaCamaras(pagina); // Obtenemos la pagina
                }, executor)) // Bloque de hilos que usamos, declarado en AsyncConfig
                .toList(); // Enlistamos

        // Eliminacion de metadatos y concatenacion de resultados
        return Stream.concat( // Concatenamos cada pagina mediante flujos de datos
                Stream.of(primeraPagina),
                futurasPaginas.stream().map(CompletableFuture::join) // Esperamos que se complete el future
        )
                .map(OpenDataCameraResponse::cameras) // Extraemos la lista de camaras de la respuesta
                .filter(Objects::nonNull) // Filtramos valores nulos
                .flatMap(Collection::stream) // Eliminamos el resto de datos y unimos todas las camaras en un stream
                .toList(); // Enlistamos
    }

    // Prepara los dtos y cachea los recursos para que no entren en conflicto
    private Map<Integer, Recurso> prepararYcachearRecursos(List<OpenDataCamera> todosLosDtos) {
        Map<Integer, OpenDataSource> recursosDtoMap = apiTrafico.listaRecursos().stream() // Abre un flujo de datos con
                                                                                          // los recursos de la api
                .collect( // Crea una coleccion con los recursos
                        Collectors.toMap( // Mapea los recursos
                                OpenDataSource::id, // Les asigna su id como clave
                                Function.identity(), // Asigna el recurso como valor
                                (existente, nuevo) -> existente) // Comprueba si se repite para no devolverlo
                );

        Set<Integer> idsRecursosNecesarios = todosLosDtos.stream() // Abre un flujo de datos con las camaras
                .map(OpenDataCamera::sourceId) // Mapea los id de los recuros
                .collect(Collectors.toSet()); // Setea los id como coleccion

        Map<Integer, Recurso> recursosExistentes = recursoRepository.findAllById(idsRecursosNecesarios).stream() // Abre
                                                                                                                 // un
                                                                                                                 // flujo
                                                                                                                 // de
                                                                                                                 // datos
                                                                                                                 // con
                                                                                                                 // los
                                                                                                                 // idNecesarios
                                                                                                                 // que
                                                                                                                 // ya
                                                                                                                 // esten
                                                                                                                 // en
                                                                                                                 // bd
                .collect(
                        Collectors.toMap(Recurso::getId, Function.identity())); // Genera una coleccion con los ids
                                                                                // necesarios partiendo de los
                                                                                // existentes
        log.debug("🔍 Encontrados {} recursos existentes en la BD.", recursosExistentes.size());

        // Genera los recursos necesarios
        Map<Integer, Recurso> mapaDeRecursosFinal = new HashMap<>(recursosExistentes);
        for (Integer idNecesario : idsRecursosNecesarios) {
            if (!mapaDeRecursosFinal.containsKey(idNecesario)) {
                OpenDataSource dto = recursosDtoMap.get(idNecesario);
                if (dto != null) {
                    Recurso nuevoRecurso = recursoMapper.toEntity(dto);
                    mapaDeRecursosFinal.put(idNecesario, nuevoRecurso);
                }
            }
        }
        return mapaDeRecursosFinal;
    }

    private List<Camera> validarYmapear(List<OpenDataCamera> todosLosDtos, Map<Integer, Recurso> mapaDeRecursosFinal) {
        List<CompletableFuture<Camera>> camarasValidadasFutures = todosLosDtos.stream()
                .map(dto -> CompletableFuture.supplyAsync(() -> {
                    log.debug("⬇️ Revisando imagen camara {} de recurso {} ", dto.cameraId(), dto.sourceId()); // Opcional
                                                                                                               // reducir
                                                                                                               // logs
                    if (cameraValidator.isValid(dto)) {
                        log.debug("✅ Imagen valida camara {} de recurso {} ", dto.cameraId(), dto.sourceId());
                        try {
                            Recurso recurso = mapaDeRecursosFinal.get(dto.sourceId());
                            if (recurso != null) {
                                // 1. Convertimos DTO a Entidad
                                Camera entity = cameraMapper.toEntity(dto, recurso);

                                // 2. 🔥 NUEVO: Normalizamos coordenadas (UTM a GPS) antes de devolver
                                coordinateNormalizer.normalize(entity);

                                return entity;
                            }
                        } catch (Exception e) {
                            log.error("❌ Error mapeando cámara {}: {}", dto.cameraId(), e.getMessage());
                        }
                    }
                    log.debug("❌ Imagen no valida camara {} de recurso {} ", dto.cameraId(), dto.sourceId());
                    return null;
                }, executor))
                .toList();

        return camarasValidadasFutures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional // Importante para que JPA gestione el contexto
    private void persistenciaDatos(List<Camera> camarasValidadas) {
        if (camarasValidadas.isEmpty())
            return;

        // 1. Extraemos todos los IDs externos de las cámaras que llegan
        List<Integer> idsExternos = camarasValidadas.stream()
                .map(Camera::getId)
                .toList();

        // 2. JPA: Traemos de la DB las posibles coincidencias (Entidades GESTIONADAS)
        List<Camera> candidatasDB = cameraRepository.findCandidatasPorIdsExternos(idsExternos);

        // 3. Creamos un Mapa para búsqueda rápida: Clave "idExterno-idRecurso" ->
        // Objeto Camera
        Map<String, Camera> mapaExistentes = candidatasDB.stream()
                .collect(Collectors.toMap(
                        c -> generarClaveUnica(c),
                        c -> c));

        List<Camera> listaFinalParaGuardar = new ArrayList<>();

        // 4. Procesamos la lista que llegó de la API
        for (Camera camaraEntrante : camarasValidadas) {
            String claveLogica = generarClaveUnica(camaraEntrante);

            Camera camaraEnDB = mapaExistentes.get(claveLogica);

            if (camaraEnDB != null) {
                // --- CASO UPDATE ---
                // Usamos el objeto DE LA DB (que tiene el id_model interno) y le pegamos los
                // datos nuevos
                actualizarDatos(camaraEnDB, camaraEntrante);
                camaraEnDB.setNew(false);
                camaraEnDB.setUltimaActualizacion(LocalDateTime.now());

                listaFinalParaGuardar.add(camaraEnDB);
            } else {
                // --- CASO INSERT ---
                // Es totalmente nueva, no existe esa combinación ID + Recurso
                camaraEntrante.setNew(true);
                camaraEntrante.setPrimeraInsercion(LocalDateTime.now());
                camaraEntrante.setUltimaActualizacion(LocalDateTime.now());

                listaFinalParaGuardar.add(camaraEntrante);
            }
        }

        // 5. Guardamos todo en lote. JPA sabe cuáles son updates y cuáles inserts
        if (!listaFinalParaGuardar.isEmpty()) {
            cameraRepository.saveAll(listaFinalParaGuardar);
            log.debug("✅ Persistencia finalizada: {} cámaras procesadas.", listaFinalParaGuardar.size());
        }
    }

    // Helper para generar la clave compuesta consistente
    private String generarClaveUnica(Camera c) {
        // Si el recurso es null, maneja la excepción o usa "0"
        int idRecurso = (c.getRecurso() != null) ? c.getRecurso().getId() : 0;
        return c.getId() + "_" + idRecurso;
    }

    // Helper para copiar propiedades (sin tocar IDs ni fechas de creación)
    private void actualizarDatos(Camera destino, Camera origen) {
        destino.setNombre(origen.getNombre());
        destino.setDireccion(origen.getDireccion());
        destino.setKilometro(origen.getKilometro());
        destino.setLatitud(origen.getLatitud());
        destino.setLongitud(origen.getLongitud());
        destino.setCarretera(origen.getCarretera());
        destino.setUrlImage(origen.getUrlImage());
        // NO tocamos 'id_model' (PK interna)
        // NO tocamos 'primeraInsercion'
    }

    @Cacheable("camerasAPI")
    @Retryable(maxRetries = 3)
    @Transactional
    public List<Camera> syncAllCamerasFromAPI() {
        // --- FASE 1: OBTENCIÓN DE DATOS CRUDOS --- (Asincrona)
        log.info("📦 INICIANDO FASE 1. Descarga de datos de la API de OpenData");
        List<OpenDataCamera> todosLosDtos = obtencionDeDatosCrudos();
        log.info("✅ FASE 1 COMPLETADA. Total cámaras crudas descargadas: {}", todosLosDtos.size());

        // --- FASE 2: PREPARACIÓN DE RECURSOS --- (Sincrona)
        log.info("🛠️ INICIO FASE 2: Preparando y cacheadando recursos");
        Map<Integer, Recurso> mapaDeRecursosFinal = prepararYcachearRecursos(todosLosDtos);
        log.info("✅ FASE 2 COMPLETADA. Mapa de recursos final contiene {} entradas.", mapaDeRecursosFinal.size());

        // --- FASE 3: VALIDACIÓN Y MAPEO EN PARALELO ---
        log.info("⚡ INICIO FASE 3: Validando y Mapeando en paralelo...");
        List<Camera> camarasValidadas = validarYmapear(todosLosDtos, mapaDeRecursosFinal);
        log.info("✅ FASE 3: Camaras validadas y mapeadas");

        // --- FASE 4: PERSISTENCIA ---
        log.info("💾 INICIO FASE 4: Persistiendo cámaras...");
        persistenciaDatos(camarasValidadas);
        log.info("✅ FASE 4 COMPLETADA: Cámaras guardadas con exito");

        log.info("🏁 FIN: Sincronización de cámaras. Total cámaras procesadas: {}", camarasValidadas.size());
        return camarasValidadas;
    }

    public void changeVisibility(int id) {
        Camera cam = cameraRepository.findById(id).get();
        if (cam.getEstado() == Estado.ACTIVA) {
            cam.setEstado(Estado.INACTIVA);
        } else {
            cam.setEstado(Estado.ACTIVA);
        }
        cameraRepository.save(cam);
    }

    public void setVisibility(int id, Estado estado) {
        cameraRepository.findById(id).ifPresent(opt -> {
            opt.setEstado(estado);
            cameraRepository.save(opt);
        });
    }
}
