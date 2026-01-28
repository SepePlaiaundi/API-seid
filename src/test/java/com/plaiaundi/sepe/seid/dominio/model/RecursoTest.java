package com.plaiaundi.sepe.seid.dominio.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecursoTest {

    @Test
    void testGettersAndSetters() {
        Recurso recurso = new Recurso();
        recurso.setId(1);
        recurso.setDescEs("Castellano");
        recurso.setDescEu("Euskera");

        assertEquals(1, recurso.getId());
        assertEquals("Castellano", recurso.getDescEs());
        assertEquals("Euskera", recurso.getDescEu());

        Recurso recurso2 = new Recurso(2, "ES", "EU");
        assertEquals(2, recurso2.getId());
    }
}
