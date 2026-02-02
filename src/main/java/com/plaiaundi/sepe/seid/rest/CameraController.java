package com.plaiaundi.sepe.seid.rest;

import java.util.List;

import com.plaiaundi.sepe.seid.dominio.services.CameraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.plaiaundi.sepe.seid.dominio.model.Estado;
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

    @GetMapping(value = "/mobile", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Camera> listaCamarasMobile() {
        log.info("GET /camara/mobile");
        return cameraService.getCamerasByEstado(Estado.ACTIVA);
    }

    @GetMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Camera> listaCamarasAdmin() {
        log.info("GET /camara/admin");
        return cameraService.getAllCameras();
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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Camera> getById(@PathVariable Integer id) {
        return cameraService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Camera create(@RequestBody Camera camera) {
        return cameraService.save(camera);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Camera createMultipart(@ModelAttribute Camera camera) {
        return cameraService.save(camera);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Camera> update(@PathVariable Integer id, @RequestBody Camera camera) {
        return processUpdate(id, camera);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Camera> updateMultipart(@PathVariable Integer id, @ModelAttribute Camera camera) {
        return processUpdate(id, camera);
    }

    private ResponseEntity<Camera> processUpdate(Integer id, Camera camera) {
        return cameraService.getById(id)
                .map(existing -> {
                    camera.setIdModel(id);
                    return ResponseEntity.ok(cameraService.save(camera));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (cameraService.getById(id).isPresent()) {
            cameraService.delete(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping(value = "/{id}/status")
    public ResponseEntity<Camera> toggleStatus(@PathVariable Integer id) {
        log.info("PATCH /camara/{}/status", id);
        return cameraService.toggleStatus(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping(value = "/{id}/visibility", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> toggleVisibility(@PathVariable Integer id) {
        log.info("PATCH /camara/{}/visibility", id);
        if (cameraService.getById(id).isPresent()) {
            cameraService.changeVisibility(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping(value = "/{id}/{estado}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> toggleVisibility(@PathVariable Integer id, @PathVariable Estado estado) {
        log.info("PATCH /camara/{}/{estado}", id, estado);
        if (cameraService.getById(id).isPresent()) {
            cameraService.setVisibility(id, estado);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
