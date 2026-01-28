package com.plaiaundi.sepe.seid.rest;

import java.util.List;

import com.plaiaundi.sepe.seid.dominio.services.CameraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.plaiaundi.sepe.seid.dominio.model.Camera;

@RestController
@RequestMapping("/camara")
@CrossOrigin(origins = "*")
public class CameraController {

    private static final Logger log = LoggerFactory.getLogger(CameraController.class);

    private final CameraService cameraService;

    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

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
}
