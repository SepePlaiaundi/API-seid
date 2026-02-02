package com.plaiaundi.sepe.seid.rest;

import com.plaiaundi.sepe.seid.dominio.services.CameraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CameraControllerTest {

    private MockMvc mockMvc;
    private CameraService cameraService;

    @BeforeEach
    void setUp() {
        cameraService = Mockito.mock(CameraService.class);
        CameraController cameraController = new CameraController(cameraService);
        mockMvc = MockMvcBuilders.standaloneSetup(cameraController).build();
    }

    @Test
    void testListaDeCamaras() throws Exception {
        when(cameraService.getCameras()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/camara")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMethodName() throws Exception {
        when(cameraService.syncAllCamerasFromAPI()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/camara/tunel")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testPostMethodName() throws Exception {
        mockMvc.perform(post("/camara/sync")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testToggleVisibility() throws Exception {
        int cameraId = 1;
        when(cameraService.changeVisibility(cameraId)).thenReturn(true);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .patch("/camara/" + cameraId + "/visibility")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
