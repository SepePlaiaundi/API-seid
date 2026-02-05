package com.plaiaundi.sepe.seid.dominio.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plaiaundi.sepe.seid.dominio.dao.IncidenceRepository;
import com.plaiaundi.sepe.seid.dominio.dao.RecursoRepository;
import com.plaiaundi.sepe.seid.dominio.model.Estado;
import com.plaiaundi.sepe.seid.dominio.model.Incidence;
import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dominio.model.Response;
import com.plaiaundi.sepe.seid.dominio.util.IncidenceValidator;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidence;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidenceResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataSource;
import com.plaiaundi.sepe.seid.infrastructure.ApiTrafico;
import com.plaiaundi.sepe.seid.infrastructure.CoordinateNormalizer;
import com.plaiaundi.sepe.seid.infrastructure.mappers.IncidenceMapper;
import com.plaiaundi.sepe.seid.infrastructure.mappers.RecursoMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IncidenceService {

    private static final Logger log = LoggerFactory.getLogger(IncidenceService.class);

    private final ApiTrafico apiTrafico;
    private final IncidenceRepository incidenceRepository;
    private final RecursoRepository recursoRepository;
    private final IncidenceValidator incidenceValidator;
    private final IncidenceMapper incidenceMapper;
    private final RecursoMapper recursoMapper;
    private final CoordinateNormalizer coordinateNormalizer;
    private final Executor executor;

    public IncidenceService(ApiTrafico apiTrafico,
            IncidenceRepository incidenceRepository,
            RecursoRepository recursoRepository,
            IncidenceValidator incidenceValidator,
            IncidenceMapper incidenceMapper,
            RecursoMapper recursoMapper,
            CoordinateNormalizer coordinateNormalizer,
            @Qualifier("hilosIncidencias") Executor executor) {
        this.apiTrafico = apiTrafico;
        this.incidenceRepository = incidenceRepository;
        this.recursoRepository = recursoRepository;
        this.incidenceValidator = incidenceValidator;
        this.incidenceMapper = incidenceMapper;
        this.recursoMapper = recursoMapper;
        this.coordinateNormalizer = coordinateNormalizer;
        this.executor = executor;
    }

    private static final int MAX_PAGINAS = 100;

    public List<Incidence> getIncidences() {
        return incidenceRepository.findAllByEstado(Estado.ACTIVA);
    }

    public List<Incidence> getIncidences(LocalDateTime since) {
        return incidenceRepository.findAllByEstadoAndUltimaActualizacionAfter(Estado.ACTIVA, since);
    }

    private List<OpenDataIncidence> obtencionDeDatosCrudos() {
        // Descarga de pagina inicial para obtencion de metadata
        log.debug("📄 [Main Thread] Descargando página 1 (Síncrona)...");
        LocalDateTime now = LocalDateTime.now();
        OpenDataIncidenceResponse primeraPagina = apiTrafico.listaIncidenciasPorFecha(now.getYear(),
                now.getMonthValue(), now.getDayOfMonth());
        int totalPaginas = primeraPagina.totalPages();
        totalPaginas = totalPaginas > MAX_PAGINAS ? MAX_PAGINAS : totalPaginas;
        log.debug("📚 Total páginas detectadas: {}", totalPaginas);

        // Descarga paralela de paginas de opendata
        List<CompletableFuture<OpenDataIncidenceResponse>> futurasPaginas = IntStream
                .rangeClosed(2, totalPaginas) // Abrimos un Stream de 2 al total de paginas
                .mapToObj(pagina -> CompletableFuture.supplyAsync(() -> { // Mapeamos cada pagina como un objeto
                                                                          // completable
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
        Map<Integer, OpenDataSource> recursosDtoMap = apiTrafico.listaRecursos().stream() // Abre un flujo de datos con
                                                                                          // los recursos de la api
                .collect( // Crea una coleccion con los recursos
                        Collectors.toMap( // Mapea los recursos
                                OpenDataSource::id, // Les asigna su id como clave
                                Function.identity(), // Asigna el recurso como valor
                                (existente, nuevo) -> existente) // Comprueba si se repite para no devolverlo
                );

        Set<Integer> idsRecursosNecesarios = todosLosDtos.stream() // Abre un flujo de datos con las camaras
                .map(OpenDataIncidence::sourceId) // Mapea los id de los recuros
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

    private List<Incidence> validarYmapear(List<OpenDataIncidence> todosLosDtos,
            Map<Integer, Recurso> mapaDeRecursosFinal) {
        List<CompletableFuture<Incidence>> incidencesValidadasFutures = todosLosDtos.stream()
                .map(dto -> CompletableFuture.supplyAsync(() -> {
                    try {
                        Recurso recurso = mapaDeRecursosFinal.get(dto.sourceId());
                        if (recurso != null) {
                            // 1. Convertimos DTO a Entidad
                            Incidence entity = incidenceMapper.toEntity(dto, recurso);

                            // 2. 🔥 NUEVO: Normalizamos coordenadas (UTM a GPS) antes de devolver
                            coordinateNormalizer.normalize(entity);

                            if (entity.getLatitud() == 0.0 && entity.getLongitud() == 0.0) {
                                return null;
                            }

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
        if (incidencesValidadas.isEmpty())
            return;

        // 1. Extraemos todos los IDs externos de las cámaras que llegan
        List<Integer> idsExternos = incidencesValidadas.stream()
                .map(Incidence::getExternalId)
                .toList();

        // 2. JPA: Traemos de la DB las posibles coincidencias (Entidades GESTIONADAS)
        List<Incidence> candidatasDB = incidenceRepository.findCandidatasPorIdsExternos(idsExternos);

        // 3. Creamos un Mapa para búsqueda rápida: Clave "idExterno-idRecurso" ->
        // Objeto Camera
        Map<String, Incidence> mapaExistentes = candidatasDB.stream()
                .collect(Collectors.toMap(
                        c -> generarClaveUnica(c),
                        c -> c));

        List<Incidence> listaFinalParaGuardar = new ArrayList<>();

        // 4. Procesamos la lista que llegó de la API
        for (Incidence incidenciaEntrante : incidencesValidadas) {
            String claveLogica = generarClaveUnica(incidenciaEntrante);

            Incidence incidenciaEnDB = mapaExistentes.get(claveLogica);

            if (incidenciaEnDB != null) {
                // --- CASO UPDATE ---
                // Usamos el objeto DE LA DB (que tiene el id_model interno) y le pegamos los
                // datos nuevos
                if (actualizarDatos(incidenciaEnDB, incidenciaEntrante)) {
                    incidenciaEnDB.setUltimaActualizacion(LocalDateTime.now());
                    listaFinalParaGuardar.add(incidenciaEnDB);
                } else {
                    // Sin cambios, no actualizamos fecha ni guardamos (persistencia innecesaria)
                }
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
        return c.getExternalId() + "_" + idRecurso;
    }

    // Helper para copiar propiedades (sin tocar IDs ni fechas de creación)
    private boolean actualizarDatos(Incidence destino, Incidence origen) {
        boolean cambiado = false;

        if (!Objects.equals(destino.getProvincia(), origen.getProvincia())) {
            destino.setProvincia(origen.getProvincia());
            cambiado = true;
        }
        if (!Objects.equals(destino.getCausa(), origen.getCausa())) {
            destino.setCausa(origen.getCausa());
            cambiado = true;
        }
        if (!Objects.equals(destino.getCiudad(), origen.getCiudad())) {
            destino.setCiudad(origen.getCiudad());
            cambiado = true;
        }
        if (!Objects.equals(destino.getFecIni(), origen.getFecIni())) {
            destino.setFecIni(origen.getFecIni());
            cambiado = true;
        }
        if (!Objects.equals(destino.getFecFin(), origen.getFecFin())) {
            destino.setFecFin(origen.getFecFin());
            cambiado = true;
        }
        if (!Objects.equals(destino.getCarretera(), origen.getCarretera())) {
            destino.setCarretera(origen.getCarretera());
            cambiado = true;
        }
        if (!Objects.equals(destino.getDireccion(), origen.getDireccion())) {
            destino.setDireccion(origen.getDireccion());
            cambiado = true;
        }
        if (Double.compare(destino.getLatitud(), origen.getLatitud()) != 0) {
            destino.setLatitud(origen.getLatitud());
            cambiado = true;
        }
        if (Double.compare(destino.getLongitud(), origen.getLongitud()) != 0) {
            destino.setLongitud(origen.getLongitud());
            cambiado = true;
        }
        if (!Objects.equals(destino.getNivel(), origen.getNivel())) {
            destino.setNivel(origen.getNivel());
            cambiado = true;
        }
        if (!Objects.equals(destino.getTipo(), origen.getTipo())) {
            destino.setTipo(origen.getTipo());
            cambiado = true;
        }
        if (!Objects.equals(destino.getDescripcion(), origen.getDescripcion())) {
            destino.setDescripcion(origen.getDescripcion());
            cambiado = true;
        }

        // Si quieres actualizar también la relación del recurso:
        // Comparamos por ID para evitar problemas de instancias
        Integer idRecursoDest = (destino.getRecurso() != null) ? destino.getRecurso().getId() : null;
        Integer idRecursoOrig = (origen.getRecurso() != null) ? origen.getRecurso().getId() : null;
        if (!Objects.equals(idRecursoDest, idRecursoOrig)) {
            destino.setRecurso(origen.getRecurso());
            cambiado = true;
        }

        return cambiado;
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

    public Optional<Incidence> getById(Integer id) {
        return incidenceRepository.findById(id);
    }

    public void delete(Integer id) {
        incidenceRepository.findById(id).ifPresent(opt -> {
            opt.setEstado(Estado.ELIMINADA);
            incidenceRepository.save(opt);
        });
    }

    public List<Incidence> getIncidences(String tipo) {
        return incidenceRepository.findAllByTipo(tipo);
    }

    public Response save(Incidence incidencia) {
        Response respuesta = new Response();
        try {
            // Aseguramos que el recurso esté completo si viene solo con ID
            if (incidencia.getRecurso() != null) {
                recursoRepository.findById(incidencia.getRecurso().getId())
                        .ifPresent(incidencia::setRecurso);
            }

            incidencia = incidenceValidator.validar(incidencia);

            if (incidencia.getId() == null) {
                incidencia.setUltimaActualizacion(LocalDateTime.now());
            }

            if (incidencia.getFecIni() == null) {
                incidencia.setFecIni(LocalDateTime.now());
            }

            incidenceRepository.save(incidencia);
            respuesta.setMensaje("Incidencia guardada correctamente");
        } catch (Exception e) {
            respuesta.setMensaje("Ha ocurrido un error al guardar la incidencia");
        } finally {
            return respuesta;
        }
    }

    public Response update(Integer id, Incidence incidenceDetails) {
        Response response = new Response();
        try {
            Incidence existingIncidence = incidenceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Incidence not found"));

            // Actualizar campos
            existingIncidence.setProvincia(incidenceDetails.getProvincia());
            existingIncidence.setCausa(incidenceDetails.getCausa());
            existingIncidence.setFecIni(incidenceDetails.getFecIni());
            existingIncidence.setCarretera(incidenceDetails.getCarretera());
            existingIncidence.setDireccion(incidenceDetails.getDireccion());
            existingIncidence.setLatitud(incidenceDetails.getLatitud());
            existingIncidence.setLongitud(incidenceDetails.getLongitud());
            existingIncidence.setCiudad(incidenceDetails.getCiudad());
            existingIncidence.setFecFin(incidenceDetails.getFecFin());
            existingIncidence.setNivel(incidenceDetails.getNivel());
            existingIncidence.setTipo(incidenceDetails.getTipo());
            existingIncidence.setDescripcion(incidenceDetails.getDescripcion());

            // Recurso
            if (incidenceDetails.getRecurso() != null) {
                recursoRepository.findById(incidenceDetails.getRecurso().getId())
                        .ifPresent(existingIncidence::setRecurso);
            }

            // Estado
            if (incidenceDetails.getEstado() != null) {
                existingIncidence.setEstado(incidenceDetails.getEstado());
            }

            // Fechas
            existingIncidence.setUltimaActualizacion(LocalDateTime.now());

            incidenceRepository.save(existingIncidence);
            response.setMensaje("Incidencia actualizada correctamente");
        } catch (Exception e) {
            response.setMensaje("Error al actualizar la incidencia: " + e.getMessage());
        }
        return response;
    }

}
