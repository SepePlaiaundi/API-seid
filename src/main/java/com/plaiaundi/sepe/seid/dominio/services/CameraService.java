package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.net.MalformedURLException;
import java.net.URL;

@Service
@Transactional
public class CameraService {

    @Autowired
    private CameraRepository cameraRepository;
    private final RestClient restClient = RestClient.create();

    public Camera parseFromOpenDataCamera(OpenDataCamera camara) {
        if (!validarDatos(camara)) return null;
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
            return cam;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private boolean validarDatos(OpenDataCamera camara) {
        return
            camara.cameraId() != null &&
            camara.latitude() != null &&
            camara.longitude() != null &&
            urlCamaraValida(camara) &&
            camara.cameraName() != null;
    }

    private boolean urlCamaraValida(OpenDataCamera camara) {
        String url = camara.urlImage();

        if (url == null) return false;

        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        return urlIsOk(url);

    }

    private boolean urlIsOk(String url) {
        int code = getStatusCode(url);
        return code > 199 && code < 299;
    }

    private int getStatusCode(String url) {
        try {
            ResponseEntity<Void> response = restClient.head()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity(); // No descarga la imagen, solo recupera headers y status

            return response.getStatusCode().value();
        } catch (Exception e) {
            // Maneja 404, 403, etc., capturando la excepción de Spring
            System.err.println("Error al recuperar la imagen: " + e.getMessage());
            return 500;
        }
    }

}
