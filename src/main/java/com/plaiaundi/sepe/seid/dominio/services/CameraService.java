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
        try {
            cam.setId(camara.cameraId());
            cam.setCarretera(camara.road());
            cam.setDireccion(camara.address());
            cam.setKilometro(camara.kilometer());
            cam.setLatitud(camara.latitude());
            cam.setLongitud(camara.longitude());
            cam.setNombre(camara.cameraName());

            String url = camara.urlImage();
            if (url.startsWith("http://www.trafikoa.net")) {
                url = url.replace("http://www.trafikoa.net", "https://apps.trafikoa.euskadi.eus");
            }
            cam.setUrlImage(new URL(url));

            // Guardamos en BD
            cameraRepository.save(cam);

            log.info("Cámara guardada: " + cam.getId() + " - " + Thread.currentThread().getName());

            return CompletableFuture.completedFuture(cam);

        } catch (MalformedURLException e) {
            return CompletableFuture.completedFuture(null);
        }
    }

    // --- Tus métodos privados siguen igual (se ejecutan dentro del hilo async) ---

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
        String url = camara.urlImage();
        if (url == null) return false;

        if (url.startsWith("http://www.trafikoa.net")) {
            url = url.replace("http://www.trafikoa.net", "https://apps.trafikoa.euskadi.eus");
        }

        try { new URL(url); } catch (MalformedURLException e) { return false; }

        return urlIsOk(url);
    }

    private boolean urlIsOk(String url) {
        int code = getStatusCode(url);
        return code > 199 && code < 299;
    }

    private int getStatusCode(String url) {
        try {
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