package com.plaiaundi.sepe.seid.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.plaiaundi.sepe.seid.dominio.services.ApiTrafico;
import com.plaiaundi.sepe.seid.dominio.services.CameraService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Module;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dto.OpenDataCameraResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;


@Slf4j
@RestController
@RequestMapping("/camara")
public class CameraController {

    @Autowired
    private CameraRepository cameraRepository;
    @Autowired
    private CameraService cameraService;
    @Autowired
    private ApiTrafico apiTrafico;

    private final RestClient restClient;

    public CameraController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Camera> listaDeCamaras() {
        log.info("GET /camara");
        // Respuesta instantánea desde MySQL (milisegundos)
        // Ya no hay riesgo de excepciones de red ni esperas.
        return cameraRepository.findAll();
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
        return cameraRepository.findById(id);
    }

}
