package com.plaiaundi.sepe.seid.rest;

import java.util.List;

import com.plaiaundi.sepe.seid.dominio.services.IncidenceService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.plaiaundi.sepe.seid.dominio.model.Incidence;




@Slf4j
@RestController
@RequestMapping("/incidencia")
@CrossOrigin(origins = "*")
public class IncidenceController {

    @Autowired
    private IncidenceService incidenceService;

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
    

    
}
