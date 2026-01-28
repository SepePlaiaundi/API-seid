package com.plaiaundi.sepe.seid.dominio.model;

import org.junit.jupiter.api.Test;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CameraTest {

    @Test
    void testGettersAndSetters() throws MalformedURLException {
        Camera camera = new Camera();
        Recurso recurso = new Recurso(1, "Desc ES", "Desc EU");
        URL url = new URL("http://example.com/img.jpg");
        LocalDateTime now = LocalDateTime.now();

        camera.setId(101);
        camera.setNombre("Cam 1");
        camera.setDireccion("Dir 1");
        camera.setKilometro("Km 1");
        camera.setLatitud(10.0);
        camera.setLongitud(20.0);
        camera.setCarretera("A-1");
        camera.setRecurso(recurso);
        camera.setUrlImage(url);
        camera.setEstado(Estado.ACTIVA);
        camera.setPrimeraInsercion(now);
        camera.setUltimaActualizacion(now);
        camera.setIdModel(99);

        assertEquals(101, camera.getId());
        assertEquals("Cam 1", camera.getNombre());
        assertEquals("Dir 1", camera.getDireccion());
        assertEquals("Km 1", camera.getKilometro());
        assertEquals(10.0, camera.getLatitud());
        assertEquals(20.0, camera.getLongitud());
        assertEquals("A-1", camera.getCarretera());
        assertEquals(recurso, camera.getRecurso());
        assertEquals(url, camera.getUrlImage());
        assertEquals(Estado.ACTIVA, camera.getEstado());
        assertEquals(now, camera.getPrimeraInsercion());
        assertEquals(now, camera.getUltimaActualizacion());
        assertEquals(99, camera.getIdModel());
    }
}
