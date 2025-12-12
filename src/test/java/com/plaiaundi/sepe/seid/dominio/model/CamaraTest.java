package com.plaiaundi.sepe.seid.dominio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CamaraTest {

    @Test
    void testConstructor() {
        Camara cam = new Camara("t","e","s","t"," ","o","k");
        assertNotEquals(null, cam);
    }

    @Test
    void testGetCarretera() {
        Camara cam = new Camara();
        String dato = "AP-8";
        cam.setCarretera(dato);
        assertEquals(dato, cam.getCarretera());
    }

    @Test
    void testGetDireccion() {
        Camara cam = new Camara();
        String dato = "Calle de la piruleta";
        cam.setDireccion(dato);
        assertEquals(dato, cam.getDireccion());

    }

    @Test
    void testGetId() {
        Camara cam = new Camara();
        String dato = "2345RTG";
        cam.setId(dato);
        assertEquals(dato, cam.getId());
    }

    @Test
    void testGetKilometro() {
        Camara cam = new Camara();
        String dato = "Km. 20";
        cam.setKilometro(dato);
        assertEquals(dato, cam.getKilometro());
    }

    @Test
    void testGetLatitud() {
        Camara cam = new Camara();
        String dato = "846531";
        cam.setLatitud(dato);
        assertEquals(dato, cam.getLatitud());
    }

    @Test
    void testGetLongitud() {
        Camara cam = new Camara();
        String dato = "641523";
        cam.setLongitud(dato);
        assertEquals(dato, cam.getLongitud());
    }

    @Test
    void testGetNombre() {
        Camara cam = new Camara();
        String dato = "Camara 01";
        cam.setNombre(dato);
        assertEquals(dato, cam.getNombre());
    }

}
