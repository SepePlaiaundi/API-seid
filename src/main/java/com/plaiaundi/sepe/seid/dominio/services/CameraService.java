package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CameraService {

    @Autowired
    private CameraRepository cameraRepository;

    // RestClient es thread-safe, puede ser estático o un bean inyectado
    private final RestClient restClient = RestClient.create();

    // --- AQUI ESTÁ LA MAGIA ---
    @Async("hilosCamaras") // Esto hace que se ejecute en otro hilo
    @Transactional // La transacción se abre dentro del hilo nuevo
    public CompletableFuture<Camera> parseFromOpenDataCamera(OpenDataCamera camara) {

        // 1. Validaciones (incluye la llamada lenta de red getStatusCode)
        if (!validarDatos(camara)) {
            // Si falla, devolvemos null envuelto en un futuro
            return CompletableFuture.completedFuture(null);
        }

        Camera cam = new Camera();
        cam.setId(camara.cameraId());
        cam.setCarretera(camara.road());
        cam.setDireccion(camara.address());
        cam.setKilometro(camara.kilometer());
        cam.setLatitud(camara.latitude());
        cam.setLongitud(camara.longitude());
        cam.setNombre(camara.cameraName());

        try {
            cam.setUrlImage(fromOpenDataCameraToURL(camara));
        } catch (Exception e) {
            log.warn("URL erronea: " + camara.urlImage());
            return CompletableFuture.completedFuture(null);
        }

        // Guardamos en BD
        cameraRepository.save(cam);

        log.info("Cámara guardada: " + cam.getId() + " - " + Thread.currentThread().getName());

        return CompletableFuture.completedFuture(cam);
    }

    // --- Tus métodos privados siguen igual (se ejecutan dentro del hilo async) ---

    private URL fromOpenDataCameraToURL(OpenDataCamera camara) throws Exception {
            String url = camara.urlImage();
            log.info("URL recibida: " + url);

            url = url.trim();

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url; 
            }

            url = parsearCamarasGuipuzkoa(url);
                        
            log.info("URL devuelta: "+url);
            return URI.create(url).toURL();
    }

    private String parsearCamarasGuipuzkoa(String url) {
        String[] inicios = {
            "https://www.trafikoa.eus",
            "http://www.trafikoa.eus",
            "https://www.trafikoa.net",
            "http://www.trafikoa.net",
        };
        
        for(String inicio : inicios) {
            if (url.startsWith(inicio)) {
                url = url.replace(inicio, "https://apps.trafikoa.euskadi.eus");
            }
        }
        return url;
    }

    private boolean validarDatos(OpenDataCamera camara) {
        // Validamos nulos rápido
        if (camara.cameraId() == null || camara.latitude() == null ||
                camara.longitude() == null || camara.cameraName() == null) {
            return false;
        }
        // Esta es la parte lenta, ahora se ejecuta en paralelo
        return urlCamaraValida(camara);
    }

    private boolean urlCamaraValida(OpenDataCamera camara) {
        URL url;
        try {
            url = fromOpenDataCameraToURL(camara);
        } catch (Exception e) {
            return false;
        }
        return urlIsOk(url);
    }

    private boolean urlIsOk(URL url) {
        int code = getStatusCode(url.toString());
        return code > 199 && code < 299;
    }

    private int getStatusCode(String url) {
        try {
            // TODO: Las camaras de guipuzkoa parsean bien pero no pasan la validacion
            // RestClient síncrono, pero como todo el método es Async, no bloquea al usuario
            ResponseEntity<Void> response = restClient.head()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity();
            return response.getStatusCode().value();
        } catch (Exception e) {
            return 500;
        }
    }
}