package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.CameraRepository;
import com.plaiaundi.sepe.seid.dominio.dao.RecursoRepository;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.model.Estado;
import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dominio.util.CameraValidator;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import com.plaiaundi.sepe.seid.dto.OpenDataCameraResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataSource;
import com.plaiaundi.sepe.seid.infrastructure.ApiTrafico;
import com.plaiaundi.sepe.seid.infrastructure.CoordinateNormalizer;
import com.plaiaundi.sepe.seid.infrastructure.mappers.CameraMapper;
import com.plaiaundi.sepe.seid.infrastructure.mappers.RecursoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CameraServiceTest {

    @Mock
    private ApiTrafico apiTrafico;
    @Mock
    private CameraRepository cameraRepository;
    @Mock
    private RecursoRepository recursoRepository;
    @Mock
    private CameraMapper cameraMapper;
    @Mock
    private RecursoMapper recursoMapper;
    @Mock
    private CameraValidator cameraValidator;
    @Mock
    private CoordinateNormalizer coordinateNormalizer;
    @Mock
    private Executor executor;

    @InjectMocks
    private CameraService cameraService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Stub executor to run tasks synchronously
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(executor).execute(any(Runnable.class));
    }

    @Test
    void shouldReturnActiveCameras() {
        Camera camera = new Camera();
        camera.setEstado(Estado.ACTIVA);
        when(cameraRepository.findAllByEstado(Estado.ACTIVA)).thenReturn(List.of(camera));

        List<Camera> result = cameraService.getCameras();

        assertEquals(1, result.size());
        verify(cameraRepository).findAllByEstado(Estado.ACTIVA);
    }

    @Test
    void shouldSyncAllCamerasFromAPI() throws MalformedURLException {
        // Mocking Phase 1: Multiple pages
        OpenDataCameraResponse page1 = mock(OpenDataCameraResponse.class);
        OpenDataCameraResponse page2 = mock(OpenDataCameraResponse.class);
        OpenDataCamera dto1 = new OpenDataCamera("Addr1", 101, "Cam1", "10", 43.0, -1.0, "Road1", 1, "url1");
        OpenDataCamera dto2 = new OpenDataCamera("Addr2", 102, "Cam2", "20", 43.1, -1.1, "Road2", 2, "url2");

        when(page1.totalPages()).thenReturn(2);
        when(page1.cameras()).thenReturn(List.of(dto1));
        when(page2.cameras()).thenReturn(List.of(dto2));

        when(apiTrafico.listaCamaras()).thenReturn(page1);
        when(apiTrafico.listaCamaras(2)).thenReturn(page2);

        // Mocking Phase 2: preparing and caching resources
        Recurso recurso1 = new Recurso(1, "Desc1", "Desc1-EU");
        when(recursoRepository.findAllById(anySet())).thenReturn(List.of(recurso1));

        OpenDataSource src2 = new OpenDataSource(2, "Desc2", "Desc2-EU");
        when(apiTrafico.listaRecursos()).thenReturn(List.of(src2));

        Recurso recurso2 = new Recurso(2, "Desc2", "Desc2-EU");
        when(recursoMapper.toEntity(any(OpenDataSource.class))).thenReturn(recurso2);

        // Mocking Phase 3: validation and mapping
        when(cameraValidator.isValid(any())).thenReturn(true);
        Camera entity1 = new Camera();
        entity1.setId(101);
        entity1.setRecurso(recurso1);
        Camera entity2 = new Camera();
        entity2.setId(102);
        entity2.setRecurso(recurso2);
        when(cameraMapper.toEntity(eq(dto1), any())).thenReturn(entity1);
        when(cameraMapper.toEntity(eq(dto2), any())).thenReturn(entity2);

        // Mocking Phase 4: persistence (Mixed update and insert)
        Camera existingInDb = new Camera();
        existingInDb.setId(101);
        existingInDb.setRecurso(recurso1);
        existingInDb.setIdModel(999);
        when(cameraRepository.findCandidatasPorIdsExternos(anyList())).thenReturn(List.of(existingInDb));

        // When
        List<Camera> result = cameraService.syncAllCamerasFromAPI();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cameraRepository).saveAll(anyList());
        verify(coordinateNormalizer, times(2)).normalize(any(Camera.class));
    }

    @Test
    void shouldFilterInvalidCameras() throws MalformedURLException {
        // Given
        OpenDataCameraResponse response = mock(OpenDataCameraResponse.class);
        OpenDataCamera dto = new OpenDataCamera("Addr", 1, "Name", "10", 43.0, -1.0, "Road", 1, "url");
        when(response.totalPages()).thenReturn(1);
        when(response.cameras()).thenReturn(List.of(dto));
        when(apiTrafico.listaCamaras()).thenReturn(response);
        when(cameraValidator.isValid(any())).thenReturn(false); // INVALID

        // When
        List<Camera> result = cameraService.syncAllCamerasFromAPI();

        // Then
        assertTrue(result.isEmpty());
        verify(cameraRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldHandleNullRecursoInClaveUnica() throws MalformedURLException {
        // Given
        OpenDataCameraResponse response = mock(OpenDataCameraResponse.class);
        OpenDataCamera dto = new OpenDataCamera("Addr", 1, "Name", "10", 43.0, -1.0, "Road", 1, "url");
        when(response.totalPages()).thenReturn(1);
        when(response.cameras()).thenReturn(List.of(dto));
        when(apiTrafico.listaCamaras()).thenReturn(response);

        Recurso rec = new Recurso(1, "R", "R");
        when(recursoRepository.findAllById(anySet())).thenReturn(List.of(rec));

        Camera entity = new Camera();
        entity.setId(1);
        entity.setRecurso(null); // NULL RECURSO
        when(cameraValidator.isValid(any())).thenReturn(true);
        when(cameraMapper.toEntity(any(), any())).thenReturn(entity);

        // When
        cameraService.syncAllCamerasFromAPI();

        // Then
        verify(cameraRepository).saveAll(anyList());
    }

    @Test
    void shouldHandleExceptionInValidarYMapear() throws MalformedURLException {
        // Given
        OpenDataCameraResponse response = mock(OpenDataCameraResponse.class);
        OpenDataCamera dto = new OpenDataCamera("Addr", 1, "Name", "10", 43.0, -1.0, "Road", 1, "url");
        when(response.totalPages()).thenReturn(1);
        when(response.cameras()).thenReturn(List.of(dto));
        when(apiTrafico.listaCamaras()).thenReturn(response);

        Recurso rec = new Recurso(1, "R", "R");
        when(recursoRepository.findAllById(anySet())).thenReturn(List.of(rec));

        when(cameraValidator.isValid(any())).thenReturn(true);
        when(cameraMapper.toEntity(any(), any())).thenThrow(new RuntimeException("Forced error"));

        // When
        cameraService.syncAllCamerasFromAPI();

        // Then
        verify(cameraRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldGetActiveCameras() {
        Camera c = new Camera();
        c.setEstado(Estado.ACTIVA);
        when(cameraRepository.findAllByEstado(Estado.ACTIVA)).thenReturn(List.of(c));
        List<Camera> result = cameraService.getCameras();
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldHandleNoResourceFound() throws MalformedURLException {
        // Given
        OpenDataCameraResponse response = mock(OpenDataCameraResponse.class);
        OpenDataCamera dto = new OpenDataCamera("Addr", 1, "Name", "10", 43.0, -1.0, "Road", 1, "url");
        when(response.totalPages()).thenReturn(1);
        when(response.cameras()).thenReturn(List.of(dto));
        when(apiTrafico.listaCamaras()).thenReturn(response);

        when(recursoRepository.findAllById(anySet())).thenReturn(Collections.emptyList());
        when(apiTrafico.listaRecursos()).thenReturn(Collections.emptyList()); // No resources in API either

        // When
        List<Camera> result = cameraService.syncAllCamerasFromAPI();

        // Then
        assertTrue(result.isEmpty());
    }
}
