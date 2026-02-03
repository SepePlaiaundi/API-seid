package com.plaiaundi.sepe.seid.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.plaiaundi.sepe.seid.dominio.services.IncidenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.plaiaundi.sepe.seid.dominio.model.Incidence;
import com.plaiaundi.sepe.seid.dominio.model.Response;

@RestController
@RequestMapping("/incidencia")
@CrossOrigin(origins = "*")
public class IncidenceController {

    private static final Logger log = LoggerFactory.getLogger(IncidenceController.class);

    private final IncidenceService incidenceService;

    public IncidenceController(IncidenceService incidenceService) {
        this.incidenceService = incidenceService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Incidence> listaDeIncidencias(
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        log.info("GET /incidencia");
        LocalDate date = (day != null && month != null && year != null)
                ? LocalDate.of(year, month, day)
                : LocalDate.now();
        LocalDateTime since = date.atStartOfDay();
        return incidenceService.getIncidences(since);
    }

    @GetMapping(value = "/tunel", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Incidence> getMethodName() {
        log.info("GET /incidencia/tunel");
        return incidenceService.syncAllIncidencesFromAPI();
    }

    @PostMapping("sync")
    public boolean postMethodName() {
        log.info("POST /incidence/sync");
        incidenceService.syncAllIncidencesFromAPI();
        return true;
    }

    @GetMapping(value = "/{tipo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Incidence> getIncidenciasByTipo(
            @PathVariable String tipo,
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        log.info("GET /incidencia/tipo/{}", tipo);
        List<Incidence> results = incidenceService.getIncidences(tipo);
        LocalDate date = (day != null && month != null && year != null)
                ? LocalDate.of(year, month, day)
                : LocalDate.now();
        LocalDateTime since = date.atStartOfDay();
        return results.stream()
                .filter(i -> i.getUltimaActualizacion() != null && i.getUltimaActualizacion().isAfter(since))
                .toList();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response guardarIncidencia(@RequestBody Incidence incidencia) {
        log.info("POST /incidencia");
        return incidenceService.save(incidencia);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response guardarIncidenciaMultipart(@ModelAttribute Incidence incidencia) {
        log.info("POST /incidencia/multipart");
        return incidenceService.save(incidencia);
    }

    @GetMapping(value = "/{id}/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Incidence> getById(@PathVariable Integer id) {
        log.info("GET /incidencia/{}/detail", id);
        return incidenceService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> update(@PathVariable Integer id, @RequestBody Incidence incidencia) {
        log.info("PUT /incidencia/{}", id);
        return processUpdate(id, incidencia);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> updateMultipart(@PathVariable Integer id, @ModelAttribute Incidence incidencia) {
        log.info("PUT /incidencia/{} (multipart)", id);
        return processUpdate(id, incidencia);
    }

    private ResponseEntity<Response> processUpdate(Integer id, Incidence incidencia) {
        Response response = incidenceService.update(id, incidencia);
        if (response.getMensaje().contains("Error")) {
            // Puedes mejorar esto devolviendo 404 si es 'not found' o 400/500 segun el
            // error
            // Por simplificar, si el mensaje indica error, devolvemos bad request o not
            // found
            if (response.getMensaje().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE /incidencia/{}", id);
        if (incidenceService.getById(id).isPresent()) {
            incidenceService.delete(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
