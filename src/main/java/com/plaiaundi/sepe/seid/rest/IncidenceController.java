package com.plaiaundi.sepe.seid.rest;

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
    public List<Incidence> listaDeIncidencias() {
        log.info("GET /incidencia");
        return incidenceService.getIncidences();
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
    public List<Incidence> getIncidencesByTipo(@PathVariable String tipo) {
        return incidenceService.getIncidences(tipo);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response guardarIncidencia(@RequestBody Incidence incidencia) {
        return incidenceService.save(incidencia);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response guardarIncidenciaMultipart(@ModelAttribute Incidence incidencia) {
        return incidenceService.save(incidencia);
    }

    @GetMapping(value = "/{id}/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Incidence> getById(@PathVariable Integer id) {
        return incidenceService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> update(@PathVariable Integer id, @RequestBody Incidence incidencia) {
        return processUpdate(id, incidencia);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> updateMultipart(@PathVariable Integer id, @ModelAttribute Incidence incidencia) {
        return processUpdate(id, incidencia);
    }

    private ResponseEntity<Response> processUpdate(Integer id, Incidence incidencia) {
        return incidenceService.getById(id)
                .map(existing -> {
                    incidencia.setIdModel(id);
                    return ResponseEntity.ok(incidenceService.save(incidencia));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (incidenceService.getById(id).isPresent()) {
            incidenceService.delete(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
