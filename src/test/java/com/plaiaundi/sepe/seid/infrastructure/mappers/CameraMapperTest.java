package com.plaiaundi.sepe.seid.infrastructure.mappers;

import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dominio.util.CameraValidator;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CameraMapperTest {

    private CameraMapper cameraMapper;
    private CameraValidator cameraValidator;

    @BeforeEach
    void setUp() {
        cameraValidator = Mockito.mock(CameraValidator.class);
        cameraMapper = new CameraMapper(cameraValidator);
    }

    @Test
    void shouldReturnNullWhenMappingNullEntity() {
        assertNull(cameraMapper.toDto(null));
    }

    @Test
    void shouldMapEntityToDto() throws MalformedURLException {
        // Given
        Recurso recurso = new Recurso(1, "Rec", "Rec");
        Camera camera = new Camera();
        camera.setId(101);
        camera.setNombre("Cam 1");
        camera.setDireccion("Dir 1");
        camera.setKilometro("Km 1");
        camera.setLatitud(10.0);
        camera.setLongitud(20.0);
        camera.setCarretera("A-1");
        camera.setRecurso(recurso);
        camera.setUrlImage(new URL("http://example.com/img.jpg"));

        // When
        OpenDataCamera dto = cameraMapper.toDto(camera);

        // Then
        assertNotNull(dto);
        assertEquals(101, dto.cameraId());
        assertEquals("Cam 1", dto.cameraName());
        assertEquals(1, dto.sourceId());
        assertEquals("http://example.com/img.jpg", dto.urlImage());
    }

    @Test
    void shouldMapEntityWithNullFieldsToDto() {
        // Given
        Camera camera = new Camera();
        camera.setId(101);
        // Recurso and UrlImage are null

        // When
        OpenDataCamera dto = cameraMapper.toDto(camera);

        // Then
        assertNotNull(dto);
        assertEquals(0, dto.sourceId());
        assertNull(dto.urlImage());
    }

    @Test
    void shouldHandleNullCleanUrl() throws MalformedURLException {
        // Given
        OpenDataCamera dto = new OpenDataCamera("Dir 1", 101, "Cam 1", "Km 1", 10.0, 20.0, "A-1", 1, null);
        Recurso recurso = new Recurso(1, "Rec", "Rec");
        when(cameraValidator.limpiarYTransformarUrl(any())).thenReturn(null);

        // When
        Camera entity = cameraMapper.toEntity(dto, recurso);

        // Then
        assertNotNull(entity);
        assertNull(entity.getUrlImage());
    }

    @Test
    void shouldMapDtoToEntity() throws MalformedURLException {
        // Given
        OpenDataCamera dto = new OpenDataCamera("Dir 1", 101, "Cam 1", "Km 1", 10.0, 20.0, "A-1", 1,
                "http://example.com/img.jpg");
        Recurso recurso = new Recurso(1, "Rec", "Rec");
        when(cameraValidator.limpiarYTransformarUrl(any())).thenReturn("http://example.com/img.jpg");

        // When
        Camera entity = cameraMapper.toEntity(dto, recurso);

        // Then
        assertNotNull(entity);
        assertEquals(101, entity.getId());
        assertEquals("Cam 1", entity.getNombre());
        assertEquals(recurso, entity.getRecurso());
        assertEquals("http://example.com/img.jpg", entity.getUrlImage().toString());
    }

    @Test
    void shouldReturnNullWhenMappingNullDto() throws MalformedURLException {
        assertNull(cameraMapper.toEntity(null, null));
    }
}
