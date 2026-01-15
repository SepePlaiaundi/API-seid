package com.plaiaundi.sepe.seid.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dto.OpenDataCameraResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;


@RestController
@RequestMapping("/camara")
public class CameraController {

    @Autowired
    private CameraRepository cameraRepository;
    private final RestClient restClient;

    public CameraController(RestClient restClient) {
        this.restClient = restClient;
    }
    
    @GetMapping("")
    public List<Camera> listaDeCamaras() {
        List<OpenDataCameraResponse> result = new ArrayList<>();
        List<OpenDataCamera> camaras = new ArrayList<>();

        // 1. Hacemos la primera petición para ver cuántas páginas hay
        OpenDataCameraResponse primeraPagina = restClient.get()
                .uri("v1.0/cameras?_page=1")
                .retrieve()
                .body(OpenDataCameraResponse.class);

        if (primeraPagina != null) {
            // 2. Leemos el total de páginas de la respuesta
            int totalPaginas = primeraPagina.totalPages();

            // 3. Iteramos desde la 2 hasta el final exacto
            for (int i = totalPaginas; i < 0; i--) {
                int paginaActual = i; // Variable efectiva final para la lambda
                
                OpenDataCameraResponse pagina = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/v1.0/cameras")
                        .queryParam("_page", paginaActual)
                        .build()
                    )
                    .retrieve()
                    .body(OpenDataCameraResponse.class);
                
                if (pagina != null) {
                    result.add(pagina);
                }
            }
        }
        
        for (OpenDataCameraResponse respuesta: result) {
            camaras.addAll(respuesta.cameras());
        }
 
        camaras.removeIf(camara -> camara.urlImage() == null);
        camaras.removeIf(camara -> camara.latitude() == null);
        camaras.removeIf(camara -> camara.longitude() == null);

        List<Camera> camarasDominio = new ArrayList<>();
        for (OpenDataCamera camara: camaras) {
            Camera cam = new Camera();
            try {
                cam.setId(          camara.cameraId());
                cam.setCarretera(   camara.road());
                cam.setDireccion(   camara.address());
                cam.setKilometro(   camara.kilometer());
                cam.setLatitud(     camara.latitude());
                cam.setLongitud(    camara.longitude());
                cam.setNombre(      camara.cameraName());
                cam.setUrlImage(    new URL(camara.urlImage()));
                cameraRepository.save(cam);
                camarasDominio.add(cam);
            } catch (MalformedURLException e) {
                continue;
            }
        }
        return camarasDominio;
    }

}
