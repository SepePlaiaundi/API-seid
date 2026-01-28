package com.plaiaundi.sepe.seid.dominio.util;

import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CameraValidatorTest {

    @Mock
    private RestClient restClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private ResponseEntity<Void> responseEntity;

    @InjectMocks
    private CameraValidator cameraValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldValidateMetadatosCorrectly() {
        OpenDataCamera validCamara = new OpenDataCamera("Addr", 1, "Name", "10", 43.0, -1.0, "Road", 1, "url");
        OpenDataCamera invalidCamara = new OpenDataCamera(null, 0, null, null, 0.0, 0.0, null, 0, null);

        // Accessing private method via public isValid (first part of the logic)
        // We mock the rest of the flow to focus on metadatos
        // But isValid calls isUrlReachable which uses restClient.
        // Let's test the components if possible, or just mock the restClient.

        // Mocking restClient behavior for head()
        when(restClient.head()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class)).retrieve().toBodilessEntity()).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);

        assertTrue(cameraValidator.isValid(validCamara));
        assertFalse(cameraValidator.isValid(invalidCamara));
    }

    @Test
    void shouldCleanAndTransformUrl() {
        String rawUrl = "www.trafikoa.net/img.jpg";
        String expected = "https://apps.trafikoa.euskadi.eus/img.jpg";

        String result = cameraValidator.limpiarYTransformarUrl(rawUrl);

        assertEquals(expected, result);
    }

    @Test
    void shouldHandleNullUrl() {
        assertThrows(IllegalArgumentException.class, () -> cameraValidator.limpiarYTransformarUrl(null));
    }

    @Test
    void shouldReturnFalseWhenUrlIsUnreachable() {
        OpenDataCamera camara = new OpenDataCamera("Addr", 1, "Name", "10", 43.0, -1.0, "Road", 1,
                "http://offline.com");

        when(restClient.head()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class)).retrieve().toBodilessEntity())
                .thenThrow(new RuntimeException("Connection failed"));

        // It should retry with GET
        when(restClient.get()).thenReturn(requestHeadersUriSpec);

        assertFalse(cameraValidator.isValid(camara));
    }
}
