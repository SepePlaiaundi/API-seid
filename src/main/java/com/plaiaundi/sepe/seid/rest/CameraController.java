package com.plaiaundi.sepe.seid.rest;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;


@Slf4j
@RestController
@RequestMapping("/camara")
public class CameraController {

    @Autowired
    private CameraRepository cameraRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Camera> listaDeCamaras() {
        log.info("GET /camara");
        // Respuesta instantánea desde MySQL (milisegundos)
        // Ya no hay riesgo de excepciones de red ni esperas.
        return cameraRepository.findAllByEstado(Camera.Estado.ACTIVA);
    }

    @GetMapping(value="/byPosition/{longitud}/{latitud}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Camera> listaDeCamaras(
            @PathVariable String longitud,
            @PathVariable String latitud
    ) {
        log.info("GET /camara/byPosition/{}/{}", longitud, latitud);
        return cameraRepository.findByLatitudAndLongitud(longitud, latitud);
    }

    @GetMapping(value="/byId/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Camera camara(
            @PathVariable String id
    ) {
        log.info("GET /camara/byPosition/{}", id);
        return cameraRepository.findById(id).get();
    }

}
