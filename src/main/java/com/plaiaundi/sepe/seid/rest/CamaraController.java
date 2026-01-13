package com.plaiaundi.sepe.seid.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import com.plaiaundi.sepe.seid.dominio.model.Camara;
import com.plaiaundi.sepe.seid.dto.OpenDataCamaraResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;


@RestController
@RequestMapping("/camara")
public class CamaraController {

    private final RestClient restClient;

    public CamaraController(RestClient restClient) {
        this.restClient = restClient;
    }
    
    @GetMapping("")
    public List<OpenDataCamera> listaDeCamaras() {
        List<OpenDataCamaraResponse> result = new ArrayList<>();
        List<OpenDataCamera> camaras = new ArrayList<>();

        // 1. Hacemos la primera petición para ver cuántas páginas hay
        OpenDataCamaraResponse primeraPagina = restClient.get()
                .uri("v1.0/cameras?_page=1")
                .retrieve()
                .body(OpenDataCamaraResponse.class);

        if (primeraPagina != null) {
            result.add(primeraPagina);
            
            // 2. Leemos el total de páginas de la respuesta
            int totalPaginas = primeraPagina.totalPages();

            // 3. Iteramos desde la 2 hasta el final exacto
            for (int i = 2; i <= totalPaginas; i++) {
                int paginaActual = i; // Variable efectiva final para la lambda
                
                OpenDataCamaraResponse pagina = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/v1.0/cameras")
                        .queryParam("_page", paginaActual)
                        .build()
                    )
                    .retrieve()
                    .body(OpenDataCamaraResponse.class);
                
                if (pagina != null) {
                    result.add(pagina);
                }
            }
        }
        
        for (OpenDataCamaraResponse respuesta: result) {
            camaras.addAll(respuesta.cameras());
        }

        return camaras;
    }

}
