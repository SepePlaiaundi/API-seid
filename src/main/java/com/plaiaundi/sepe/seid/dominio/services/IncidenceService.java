package com.plaiaundi.sepe.seid.dominio.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.dao.IncidenceRepository;
import com.plaiaundi.sepe.seid.dominio.dao.RecursoRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.model.Estado;
import com.plaiaundi.sepe.seid.dominio.model.Incidence;
import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dominio.util.CameraValidator;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import com.plaiaundi.sepe.seid.dto.OpenDataCameraResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidence;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidenceResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataSource;
import com.plaiaundi.sepe.seid.infrastructure.ApiTrafico;
import com.plaiaundi.sepe.seid.infrastructure.CoordinateNormalizer;
import com.plaiaundi.sepe.seid.infrastructure.mappers.CameraMapper;
import com.plaiaundi.sepe.seid.infrastructure.mappers.IncidenceMapper;
import com.plaiaundi.sepe.seid.infrastructure.mappers.RecursoMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IncidenceService {

    @Autowired
    private ApiTrafico apiTrafico;
    @Autowired
    private IncidenceRepository incidenceRepository;
    @Autowired
    private RecursoRepository recursoRepository;
    @Autowired
    private IncidenceMapper incidenceMapper;
    @Autowired
    private RecursoMapper recursoMapper;
    @Autowired
    private CoordinateNormalizer coordinateNormalizer;
    @Autowired
    @Qualifier("hilosIncidencias")
    private Executor executor;

    private static final int MAX_PAGINAS = 100;

    public List<Incidence> getIncidences() {
        return incidenceRepository.findAllByEstado(Estado.ACTIVA);
    }

    private List<OpenDataIncidence> obtencionDeDatosCrudos() {
        // Descarga de pagina inicial para obtencion de metadata
        log.debug("📄 [Main Thread] Descargando página 1 (Síncrona)...");
        OpenDataIncidenceResponse primeraPagina = apiTrafico.listaIncidencias();
        int totalPaginas = primeraPagina.totalPages();
        totalPaginas = totalPaginas > MAX_PAGINAS ? MAX_PAGINAS : totalPaginas;
        log.debug("📚 Total páginas detectadas: {}", totalPaginas);

        // Descarga paralela de paginas de opendata
        List<CompletableFuture<OpenDataIncidenceResponse>> futurasPaginas = IntStream
                .rangeClosed(2, totalPaginas) // Abrimos un Stream de 2 al total de paginas
                .mapToObj(pagina -> CompletableFuture.supplyAsync(() -> { // Mapeamos cada pagina como un objeto completable
                    log.debug("⬇️ [Hilo: {}] Solicitando página {}", Thread.currentThread().getName(), pagina);
                    return apiTrafico.listaIncidencias(pagina); // Obtenemos la pagina
                }, executor)) // Bloque de hilos que usamos, declarado en AsyncConfig
                .toList(); // Enlistamos

        // Eliminacion de metadatos y concatenacion de resultados
        return Stream.concat( // Concatenamos cada pagina mediante flujos de datos
                        Stream.of(primeraPagina),
                        futurasPaginas.stream().map(CompletableFuture::join) // Esperamos que se complete el future
                )
                .map(OpenDataIncidenceResponse::incidences) // Extraemos la lista de camaras de la respuesta
                .filter(Objects::nonNull) // Filtramos valores nulos
                .flatMap(Collection::stream) // Eliminamos el resto de datos y unimos todas las camaras en un stream
                .toList(); // Enlistamos
    }

    // Prepara los dtos y cachea los recursos para que no entren en conflicto
    private Map<Integer, Recurso> prepararYcachearRecursos(List<OpenDataIncidence> todosLosDtos) {
        Map<Integer, OpenDataSource> recursosDtoMap = apiTrafico.listaRecursos().stream() // Abre un flujo de datos con los recursos de la api
                .collect( //Crea una coleccion con los recursos
                    Collectors.toMap( // Mapea los recursos
                        OpenDataSource::id, // Les asigna su id como clave
                        Function.identity(), // Asigna el recurso como valor
                        (existente, nuevo) -> existente) // Comprueba si se repite para no devolverlo
                    );

        Set<Integer> idsRecursosNecesarios = todosLosDtos.stream() // Abre un flujo de datos con las camaras
                .map(OpenDataIncidence::sourceId) // Mapea los id de los recuros
                .collect(Collectors.toSet()); // Setea los id como coleccion

        Map<Integer, Recurso> recursosExistentes = recursoRepository.findAllById(idsRecursosNecesarios).stream() // Abre un flujo de datos con los idNecesarios que ya esten en bd
                .collect(
                    Collectors.toMap(Recurso::getId, Function.identity())); // Genera una coleccion con los ids necesarios partiendo de los existentes
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

    private List<Incidence> validarYmapear(List<OpenDataIncidence> todosLosDtos, Map<Integer, Recurso> mapaDeRecursosFinal) {
        List<CompletableFuture<Incidence>> incidencesValidadasFutures = todosLosDtos.stream()
                .map(dto -> CompletableFuture.supplyAsync(() -> {
                    try {
                        Recurso recurso = mapaDeRecursosFinal.get(dto.sourceId());
                        if (recurso != null) {
                            // 1. Convertimos DTO a Entidad
                            Incidence entity = incidenceMapper.toEntity(dto, recurso);
                            
                            // 2. 🔥 NUEVO: Normalizamos coordenadas (UTM a GPS) antes de devolver
                            coordinateNormalizer.normalize(entity);
                            
                            return entity;
                        }
                    } catch (Exception e) {
                        log.error("❌ Error mapeando incidencia {}: {}", dto.incidenceId(), e.getMessage());
                    }
                    return null;
                }, executor))
                .toList();

        return incidencesValidadasFutures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional // Importante para que JPA gestione el contexto
    private void persistenciaDatos(List<Incidence> incidencesValidadas) {
        if (incidencesValidadas.isEmpty()) return;

        // 1. Extraemos todos los IDs externos de las cámaras que llegan
        List<Integer> idsExternos = incidencesValidadas.stream()
                .map(Incidence::getId)
                .toList();

        // 2. JPA: Traemos de la DB las posibles coincidencias (Entidades GESTIONADAS)
        List<Incidence> candidatasDB = incidenceRepository.findCandidatasPorIdsExternos(idsExternos);

        // 3. Creamos un Mapa para búsqueda rápida: Clave "idExterno-idRecurso" -> Objeto Camera
        Map<String, Incidence> mapaExistentes = candidatasDB.stream()
                .collect(Collectors.toMap(
                    c -> generarClaveUnica(c), 
                    c -> c
                ));

        List<Incidence> listaFinalParaGuardar = new ArrayList<>();

        // 4. Procesamos la lista que llegó de la API
        for (Incidence incidenciaEntrante : incidencesValidadas) {
            String claveLogica = generarClaveUnica(incidenciaEntrante);
            
            Incidence incidenciaEnDB = mapaExistentes.get(claveLogica);

            if (incidenciaEnDB != null) {
                // --- CASO UPDATE ---
                // Usamos el objeto DE LA DB (que tiene el id_model interno) y le pegamos los datos nuevos
                actualizarDatos(incidenciaEnDB, incidenciaEntrante);
                incidenciaEnDB.setUltimaActualizacion(LocalDateTime.now());
                
                listaFinalParaGuardar.add(incidenciaEnDB);
            } else {
                // --- CASO INSERT ---
                // Es totalmente nueva, no existe esa combinación ID + Recurso
                incidenciaEntrante.setPrimeraInsercion(LocalDateTime.now());
                incidenciaEntrante.setUltimaActualizacion(LocalDateTime.now());
                
                listaFinalParaGuardar.add(incidenciaEntrante);
            }
        }

        // 5. Guardamos todo en lote. JPA sabe cuáles son updates y cuáles inserts
        if (!listaFinalParaGuardar.isEmpty()) {
            incidenceRepository.saveAll(listaFinalParaGuardar);
            log.debug("✅ Persistencia finalizada: {} incidencias procesadas.", listaFinalParaGuardar.size());
        }
    }

    // Helper para generar la clave compuesta consistente
    private String generarClaveUnica(Incidence c) {
        // Si el recurso es null, maneja la excepción o usa "0"
        int idRecurso = (c.getRecurso() != null) ? c.getRecurso().getId() : 0;
        return c.getId() + "_" + idRecurso;
    }

    // Helper para copiar propiedades (sin tocar IDs ni fechas de creación)
    private void actualizarDatos(Incidence destino, Incidence origen) {
        // Copiamos los campos de datos generales
        destino.setProvincia(origen.getProvincia());
        destino.setCausa(origen.getCausa());
        destino.setCiudad(origen.getCiudad());
        destino.setFecIni(origen.getFecIni());
        destino.setFecFin(origen.getFecFin());
        destino.setCarretera(origen.getCarretera());
        destino.setDireccion(origen.getDireccion());
        destino.setLatitud(origen.getLatitud());
        destino.setLongitud(origen.getLongitud());
        destino.setNivel(origen.getNivel());
        destino.setTipo(origen.getTipo());
        destino.setDescripcion(origen.getDescripcion());
        
        // Si quieres actualizar también la relación del recurso:
        destino.setRecurso(origen.getRecurso());
    }

    @Cacheable("incidenciasAPI")
    @Retryable(maxRetries = 3)
    @Transactional
    public List<Incidence> syncAllIncidencesFromAPI() {
        // --- FASE 1: OBTENCIÓN DE DATOS CRUDOS --- (Asincrona)
        log.info("📦 INICIANDO FASE 1. Descarga de datos de la API de OpenData");
        List<OpenDataIncidence> todosLosDtos = obtencionDeDatosCrudos();
        log.info("✅ FASE 1 COMPLETADA. Total incidencias crudas descargadas: {}", todosLosDtos.size());

        // --- FASE 2: PREPARACIÓN DE RECURSOS --- (Sincrona)
        log.info("🛠️ INICIO FASE 2: Preparando y cacheadando recursos");
        Map<Integer, Recurso> mapaDeRecursosFinal = prepararYcachearRecursos(todosLosDtos);
        log.info("✅ FASE 2 COMPLETADA. Mapa de recursos final contiene {} entradas.", mapaDeRecursosFinal.size());

        // --- FASE 3: VALIDACIÓN Y MAPEO EN PARALELO --- 
        log.info("⚡ INICIO FASE 3: Validando y Mapeando en paralelo...");
        List<Incidence> incidencesValidadas = validarYmapear(todosLosDtos, mapaDeRecursosFinal);
        log.info("✅ FASE 3: Incidencias validadas y mapeadas");

        // --- FASE 4: PERSISTENCIA ---
        log.info("💾 INICIO FASE 4: Persistiendo cámaras...");
        persistenciaDatos(incidencesValidadas);
        log.info("✅ FASE 4 COMPLETADA: Incidencias guardadas con exito");

        log.info("🏁 FIN: Sincronización de incidencias. Total incidencias procesadas: {}", incidencesValidadas.size());
        return incidencesValidadas;
    }
    
}
