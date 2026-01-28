package com.plaiaundi.sepe.seid.rest;

import com.plaiaundi.sepe.seid.dominio.model.Incidence;
import com.plaiaundi.sepe.seid.dominio.model.Response;
import com.plaiaundi.sepe.seid.dominio.services.IncidenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IncidenceControllerTest {

    private MockMvc mockMvc;
    private IncidenceService incidenceService;

    @BeforeEach
    void setUp() {
        incidenceService = Mockito.mock(IncidenceService.class);
        IncidenceController incidenceController = new IncidenceController(incidenceService);
        mockMvc = MockMvcBuilders.standaloneSetup(incidenceController).build();
    }

    @Test
    void testListaDeIncidencias() throws Exception {
        when(incidenceService.getIncidences()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/incidencia")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMethodName() throws Exception {
        when(incidenceService.syncAllIncidencesFromAPI()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/incidencia/tunel")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testPostMethodName() throws Exception {
        mockMvc.perform(post("/incidencia/sync")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetIncidencesByTipo() throws Exception {
        when(incidenceService.getIncidences("METEO")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/incidencia/METEO")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGuardarIncidencia() throws Exception {
        when(incidenceService.save(any())).thenReturn(new Response("Saved"));
        mockMvc.perform(post("/incidencia")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"descripcion\": \"Test\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
