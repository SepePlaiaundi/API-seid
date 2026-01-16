package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import com.plaiaundi.sepe.seid.dto.OpenDataCameraResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidenceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApiTrafico {

    @Autowired
    private RestClient restClient;

    public List<OpenDataCamera> getAllCameras() {
        List<OpenDataCamera> result = new ArrayList<>();

        for(int i = totalPaginasCamaras() ; i > 0 ; i--) {
            result.addAll(llamarApiCameras(i).cameras());
        }

        return filtrarCamaras(result);
    }

    private List<OpenDataCamera> filtrarCamaras(List<OpenDataCamera> lista) {
        lista.removeIf(camara -> camara.urlImage() == null);
        lista.removeIf(camara -> camara.latitude() == null);
        lista.removeIf(camara -> camara.longitude() == null);
        return lista;
    }

    private int totalPaginasCamaras() {
        return llamarApiCameras(1).totalPages();
    }

    public OpenDataCameraResponse llamarApiCameras(int numPagina) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1.0/cameras")
                        .queryParam("_page", numPagina)
                        .build()
                )
                .retrieve()
                .body(OpenDataCameraResponse.class);
    }

    public OpenDataCamera llamarApiCamerasById(int id, int idSource) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1.0/cameras/"+id+"/"+idSource)
                        .build()
                )
                .retrieve()
                .body(OpenDataCamera.class);
    }



}
