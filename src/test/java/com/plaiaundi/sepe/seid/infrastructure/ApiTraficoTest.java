package com.plaiaundi.sepe.seid.infrastructure;

import com.plaiaundi.sepe.seid.dto.OpenDataCameraResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidenceResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiTraficoTest {

    private RestClient restClient;
    private ApiTrafico apiTrafico;

    // Mocking the fluent chain of RestClient is complex,
    // but we can mock the terminal calls to reach instructions.
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private RestClient.RequestHeadersSpec requestHeadersSpec;
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        restClient = mock(RestClient.class);
        requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(any(MediaType.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        apiTrafico = new ApiTrafico(restClient);
    }

    @Test
    void testListaCamaras() {
        OpenDataCameraResponse expected = new OpenDataCameraResponse(0, 0, 0, Collections.emptyList());
        when(responseSpec.body(OpenDataCameraResponse.class)).thenReturn(expected);

        OpenDataCameraResponse result = apiTrafico.listaCamaras();

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void testListaIncidencias() {
        OpenDataIncidenceResponse expected = new OpenDataIncidenceResponse(0, 0, 0, Collections.emptyList());
        when(responseSpec.body(OpenDataIncidenceResponse.class)).thenReturn(expected);

        OpenDataIncidenceResponse result = apiTrafico.listaIncidencias();

        assertNotNull(result);
    }

    @Test
    void testListaRecursos() {
        List<OpenDataSource> expected = Collections.emptyList();
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expected);

        List<OpenDataSource> result = apiTrafico.listaRecursos();

        assertNotNull(result);
    }

    @Test
    void testListaCamarasPorLocalizacion() {
        when(responseSpec.body(OpenDataCameraResponse.class))
                .thenReturn(new OpenDataCameraResponse(0, 0, 0, Collections.emptyList()));
        assertNotNull(apiTrafico.listaCamarasPorLocalizacion(1.0, 2.0, 10));
    }

    @Test
    void testListaCamarasPorRecurso() {
        when(responseSpec.body(OpenDataCameraResponse.class))
                .thenReturn(new OpenDataCameraResponse(0, 0, 0, Collections.emptyList()));
        assertNotNull(apiTrafico.listaCamarasPorRecurso(1));
    }
}
