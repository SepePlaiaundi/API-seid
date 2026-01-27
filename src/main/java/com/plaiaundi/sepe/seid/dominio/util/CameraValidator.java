package com.plaiaundi.sepe.seid.dominio.util;

import com.plaiaundi.sepe.seid.dto.OpenDataCamera;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.apache.tomcat.websocket.ClientEndpointHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import java.net.URI;

@Component
@Slf4j
public class CameraValidator {

    @Autowired
    @Qualifier("ClienteHTTPGenerico")
    private RestClient restClient;

    /**
     * Método principal que orquesta todas las validaciones
     */
    public boolean isValid(OpenDataCamera camara) {
        // 1. Validar metadatos (ID, coords, etc.)
        if (!validarMetadatos(camara)) {
            return false;
        }

        // 2. Limpiar y transformar URL
        String urlLimpia;
        try {
            urlLimpia = limpiarYTransformarUrl(camara.urlImage());
        } catch (Exception e) {
            return false; // URL malformada
        }

        // 3. Validar conectividad (Red)
        return isUrlReachable(urlLimpia);
    }

    // --- LÓGICA DE METADATOS ---
    private boolean validarMetadatos(OpenDataCamera camara) {
        return camara.cameraId() != 0 &&
                camara.latitude() != 0.0 &&
                camara.longitude() != 0.0 &&
                camara.cameraName() != null;
    }

    // --- LÓGICA DE TRANSFORMACIÓN DE URL ---
    public String limpiarYTransformarUrl(Object rawUrlObj) {
        if (rawUrlObj == null) throw new IllegalArgumentException("URL is null");

        String url = rawUrlObj.toString().trim();

        // Asegurar protocolo
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        // Lógica específica de Guipúzcoa
        return parsearDominioGuipuzcoa(url);
    }

    private String parsearDominioGuipuzcoa(String url) {
        log.info("Parseando dominio: {}", url);
        if (url.contains("trafikoa")) {
            url = url.replaceAll(
                    "https?://www\\.trafikoa\\.(eus|net)",
                    "https://apps.trafikoa.euskadi.eus"
            );
        }
        log.info("Parseado a: {}", url);
        return url;
    }

    // --- LÓGICA DE RED ---
    private boolean isUrlReachable(String urlStr) {
        try {
            URI uri = URI.create(urlStr);
            boolean status;
            try {
                status = checkStatusCode(uri, true);

                // 2. Imprimimos la variable (ahora es seguro)
                log.info("{} Código de estado: {}", uri, status);
            // INTENTO 1: HEAD (Rápido)
                return checkStatusCode(uri, true);
            } catch (Exception e) {
                status = checkStatusCode(uri, false);
                // 2. Imprimimos la variable (ahora es seguro)
                log.info("{} Código de estado: {}", uri, status);
                return status;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private boolean checkStatusCode(URI uri, boolean useHead) {
        RestClient.RequestHeadersUriSpec<?> requestSpec = useHead ? restClient.head() : restClient.get();

        ResponseEntity<Void> response = requestSpec
                .uri(uri)
                .retrieve()
                .toBodilessEntity(); // Solo descargamos cabeceras incluso en GET

        int status = response.getStatusCode().value();
        return status >= 200 && status < 300;
    }
}