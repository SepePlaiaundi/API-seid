package com.plaiaundi.sepe.seid.rest;

import java.util.List;

import com.plaiaundi.sepe.seid.dominio.services.CameraService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;




@Slf4j
@RestController
@RequestMapping("/camara")
@CrossOrigin(origins = "*")
public class CameraController {

    @Autowired
    private CameraService cameraService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Camera> listaDeCamaras() {
        log.info("GET /camara");
        return cameraService.getCameras();
    }

    @GetMapping(value = "/tunel", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Camera> getMethodName() {
        log.info("GET /camara/tunel");
        return cameraService.syncAllCamerasFromAPI();
    }
    
    @PostMapping("sync")
    public boolean postMethodName() {
        log.info("POST /camara/sync");
        cameraService.syncAllCamerasFromAPI();
        return true;
    }
    

    /*
    @GetMapping(value="/byPosition/{longitud}/{latitud}/{radioEnKm}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Camera> listaDeCamaras(
            @PathVariable double longitud,
            @PathVariable double latitud,
            @PathVariable int radio
    ) {
        log.info("GET /camara/byPosition/{}/{}", longitud, latitud, radio);
        return cameraService.getCamerasInPosition(longitud, latitud, radio);
    }
    */

    /*
    @GetMapping(value="/byId/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Camera camara(
            @PathVariable String id
    ) {
        log.info("GET /camara/byPosition/{}", id);
        return cameraRepository.findById(id).get();
    }
    */

}
