package com.plaiaundi.sepe.seid.dominio.util;

import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.ResponseEntity;
import java.net.URI;

@Component
public class CameraValidator {

    private final RestClient restClient;

    public CameraValidator() {
        // Configuramos RestClient (puedes inyectar uno configurado globalmente si prefieres)
        this.restClient = RestClient.builder()
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)") // CRÍTICO para Guipúzcoa
                .build();
    }

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
        if (url.contains("trafikoa.eus") || url.contains("trafikoa.net")) {
            return url.replaceAll(
                    "https?://www\\.trafikoa\\.(eus|net)",
                    "https://apps.trafikoa.euskadi.eus"
            );
        }
        return url;
    }

    // --- LÓGICA DE RED ---
    private boolean isUrlReachable(String urlStr) {
        try {
            URI uri = URI.create(urlStr);

            // INTENTO 1: HEAD (Rápido)
            try {
                return checkStatusCode(uri, true);
            } catch (Exception e) {
                // INTENTO 2: GET (Lento, pero seguro si el servidor bloquea HEAD)
                // Si falla HEAD (405 Method Not Allowed o 403), intentamos GET
                return checkStatusCode(uri, false);
            }
        } catch (Exception e) {
            // Loguear solo en debug para no ensuciar logs
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