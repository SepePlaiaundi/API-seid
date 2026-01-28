package com.plaiaundi.sepe.seid.dominio.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResponseTest {

    @Test
    void testGettersAndSetters() {
        Response response = new Response();
        response.setMensaje("Success");
        assertEquals("Success", response.getMensaje());

        Response response2 = new Response("Success 2");
        assertEquals("Success 2", response2.getMensaje());
    }
}
