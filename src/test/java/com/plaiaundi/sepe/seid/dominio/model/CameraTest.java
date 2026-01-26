package com.plaiaundi.sepe.seid.dominio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CameraTest {

    @Test
    void testConstructor() {
        Camera cam = new Camera();
        assertNotEquals(null, cam);
    }

    @Test
    void testGetCarretera() {
        Camera cam = new Camera();
        String dato = "AP-8";
        cam.setCarretera(dato);
        assertEquals(dato, cam.getCarretera());
    }

    @Test
    void testGetDireccion() {
        Camera cam = new Camera();
        String dato = "Calle de la piruleta";
        cam.setDireccion(dato);
        assertEquals(dato, cam.getDireccion());

    }

    /*
     * @Test
     * void testGetId() {
     * Camera cam = new Camera();
     * String dato = "2345RTG";
     * cam.setId(dato);
     * assertEquals(dato, cam.getId());
     * }
     */

    @Test
    void testGetKilometro() {
        Camera cam = new Camera();
        String dato = "Km. 20";
        cam.setKilometro(dato);
        assertEquals(dato, cam.getKilometro());
    }

    /*
     * @Test
     * void testGetLatitud() {
     * Camera cam = new Camera();
     * String dato = "846531";
     * cam.setLatitud(dato);
     * assertEquals(dato, cam.getLatitud());
     * }
     * 
     * @Test
     * void testGetLongitud() {
     * Camera cam = new Camera();
     * String dato = "641523";
     * cam.setLongitud(dato);
     * assertEquals(dato, cam.getLongitud());
     * }
     */

    @Test
    void testGetNombre() {
        Camera cam = new Camera();
        String dato = "Camara 01";
        cam.setNombre(dato);
        assertEquals(dato, cam.getNombre());
    }

}
