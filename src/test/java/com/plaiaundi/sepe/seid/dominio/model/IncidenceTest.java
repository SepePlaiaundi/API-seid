package com.plaiaundi.sepe.seid.dominio.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class IncidenceTest {

    @Test
    void testGettersAndSetters() {
        Incidence incidence = new Incidence();
        LocalDateTime now = LocalDateTime.now();
        Recurso recurso = new Recurso(1, "Desc ES", "Desc EU");

        incidence.setId(505);
        incidence.setRecurso(recurso);
        incidence.setProvincia("Gipuzkoa");
        incidence.setCausa("OBRA");
        incidence.setFecIni(now);
        incidence.setFecFin(now.plusHours(1));
        incidence.setCarretera("N-1");
        incidence.setDireccion("Dir");
        incidence.setLatitud(1.0);
        incidence.setLongitud(2.0);
        incidence.setCiudad("Donostia");
        incidence.setNivel("NEGRO");
        incidence.setTipo("METEOROLOGICA");
        incidence.setDescripcion("Desc");
        incidence.setEstado(Estado.ACTIVA);
        incidence.setPrimeraInsercion(now);
        incidence.setUltimaActualizacion(now);
        incidence.setIdModel(1001);

        assertEquals(505, incidence.getId());
        assertEquals(recurso, incidence.getRecurso());
        assertEquals("Gipuzkoa", incidence.getProvincia());
        assertEquals("OBRA", incidence.getCausa());
        assertEquals(now, incidence.getFecIni());
        assertEquals(now.plusHours(1), incidence.getFecFin());
        assertEquals("N-1", incidence.getCarretera());
        assertEquals("Dir", incidence.getDireccion());
        assertEquals(1.0, incidence.getLatitud());
        assertEquals(2.0, incidence.getLongitud());
        assertEquals("Donostia", incidence.getCiudad());
        assertEquals("NEGRO", incidence.getNivel());
        assertEquals("METEOROLOGICA", incidence.getTipo());
        assertEquals("Desc", incidence.getDescripcion());
        assertEquals(Estado.ACTIVA, incidence.getEstado());
        assertEquals(now, incidence.getPrimeraInsercion());
        assertEquals(now, incidence.getUltimaActualizacion());
        assertEquals(1001, incidence.getIdModel());
    }
}
