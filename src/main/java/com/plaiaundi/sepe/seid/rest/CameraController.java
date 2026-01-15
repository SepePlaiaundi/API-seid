package com.plaiaundi.sepe.seid.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.plaiaundi.sepe.seid.dominio.services.ApiTrafico;
import com.plaiaundi.sepe.seid.dominio.services.CameraService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.beans.factory.annotation.Autowired;
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

    private OpenDataCameraResponse llamarApiCameras(int numPagina) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1.0/cameras")
                        .queryParam("_page", numPagina)
                        .build()
                )
                .retrieve()
                .body(OpenDataCameraResponse.class);
    }

    private List<OpenDataCameraResponse> pedirCamarasOpenDataTrafico() {
        List<OpenDataCameraResponse> result = new ArrayList<>();

        OpenDataCameraResponse primeraPagina = llamarApiCameras(1);

        if (primeraPagina == null) { return null; }

        int totalPaginas = primeraPagina.totalPages();

        for (int i = totalPaginas; i > 0; i--) {
            OpenDataCameraResponse pagina = llamarApiCameras(i);
            if (pagina != null) {
                result.add(pagina);
            }
        }

        return result;
    }

    @GetMapping
    public List<Camera> listaDeCamaras() {
        List<Camera> camarasDominio = new ArrayList<>();
        for (OpenDataCamera camara: apiTrafico.getAllCameras()) {
            Camera cam = cameraService.parseFromOpenDataCamera(camara);
            if (cam == null) { continue; }
            cameraRepository.save(cam);
            camarasDominio.add(cam);
            log.info("Insertada camara " + cam.getId());
        }
        return camarasDominio;
    }

}
