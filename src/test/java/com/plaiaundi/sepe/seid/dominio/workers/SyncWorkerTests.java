package com.plaiaundi.sepe.seid.dominio.workers;

import com.plaiaundi.sepe.seid.dominio.services.CameraService;
import com.plaiaundi.sepe.seid.dominio.services.IncidenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.ResourceAccessException;

import java.util.Collections;

import static org.mockito.Mockito.*;

class SyncWorkerTests {

    private CameraService cameraService;
    private IncidenceService incidenceService;
    private CameraSyncWorker cameraWorker;
    private IncidenceSyncWorker incidenceWorker;

    @BeforeEach
    void setUp() {
        cameraService = Mockito.mock(CameraService.class);
        incidenceService = Mockito.mock(IncidenceService.class);
        cameraWorker = new CameraSyncWorker(cameraService);
        incidenceWorker = new IncidenceSyncWorker(incidenceService);
    }

    @Test
    void testCameraWorkerSuccess() {
        when(cameraService.syncAllCamerasFromAPI()).thenReturn(Collections.emptyList());
        cameraWorker.sincronizarCamaras();
        verify(cameraService, times(1)).syncAllCamerasFromAPI();
    }

    @Test
    void testIncidenceWorkerSuccess() {
        when(incidenceService.syncAllIncidencesFromAPI()).thenReturn(Collections.emptyList());
        incidenceWorker.sincronizarCamaras();
        verify(incidenceService, times(1)).syncAllIncidencesFromAPI();
    }

    @Test
    void testCameraWorkerException() {
        // Force ResourceAccessException then success to avoid infinite recursion in
        // test
        when(cameraService.syncAllCamerasFromAPI())
                .thenThrow(new ResourceAccessException("API Down"))
                .thenReturn(Collections.emptyList());

        cameraWorker.sincronizarCamaras();

        // Should have called twice (initial + retry)
        verify(cameraService, times(2)).syncAllCamerasFromAPI();
    }

    @Test
    void testIncidenceWorkerException() {
        when(incidenceService.syncAllIncidencesFromAPI())
                .thenThrow(new ResourceAccessException("API Down"))
                .thenReturn(Collections.emptyList());

        incidenceWorker.sincronizarCamaras();

        verify(incidenceService, times(2)).syncAllIncidencesFromAPI();
    }

    @Test
    void testCameraWorkerGenericException() {
        when(cameraService.syncAllCamerasFromAPI()).thenThrow(new RuntimeException("Generic Error"));
        cameraWorker.sincronizarCamaras();
        // Should not retry on generic exception
        verify(cameraService, times(1)).syncAllCamerasFromAPI();
    }
}
