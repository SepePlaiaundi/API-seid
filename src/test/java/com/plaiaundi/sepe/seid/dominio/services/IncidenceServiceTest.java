package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.IncidenceRepository;
import com.plaiaundi.sepe.seid.dominio.dao.RecursoRepository;
import com.plaiaundi.sepe.seid.dominio.model.Incidence;
import com.plaiaundi.sepe.seid.dominio.model.Estado;
import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dominio.model.Response;
import com.plaiaundi.sepe.seid.dominio.util.IncidenceValidator;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidence;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidenceResponse;
import com.plaiaundi.sepe.seid.dto.OpenDataSource;
import com.plaiaundi.sepe.seid.infrastructure.ApiTrafico;
import com.plaiaundi.sepe.seid.infrastructure.CoordinateNormalizer;
import com.plaiaundi.sepe.seid.infrastructure.mappers.IncidenceMapper;
import com.plaiaundi.sepe.seid.infrastructure.mappers.RecursoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IncidenceServiceTest {

    @Mock
    private ApiTrafico apiTrafico;
    @Mock
    private IncidenceRepository incidenceRepository;
    @Mock
    private RecursoRepository recursoRepository;
    @Mock
    private IncidenceMapper incidenceMapper;
    @Mock
    private RecursoMapper recursoMapper;
    @Mock
    private IncidenceValidator incidenceValidator;
    @Mock
    private CoordinateNormalizer coordinateNormalizer;
    @Mock
    private Executor executor;

    @InjectMocks
    private IncidenceService incidenceService;

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
    void shouldReturnActiveIncidences() {
        Incidence incidence = new Incidence();
        incidence.setEstado(Estado.ACTIVA);
        when(incidenceRepository.findAllByEstado(Estado.ACTIVA)).thenReturn(List.of(incidence));

        List<Incidence> result = incidenceService.getIncidences();

        assertEquals(1, result.size());
        verify(incidenceRepository).findAllByEstado(Estado.ACTIVA);
    }

    @Test
    void shouldSyncAllIncidencesFromAPI() {
        // Mocking Phase 1: Multiple pages
        OpenDataIncidenceResponse page1 = mock(OpenDataIncidenceResponse.class);
        OpenDataIncidenceResponse page2 = mock(OpenDataIncidenceResponse.class);
        OpenDataIncidence dto1 = new OpenDataIncidence(1, 1, "N1", "P1", "20", "C1", "D1", LocalDateTime.now(), null,
                "R1", 0.0, 0.0, "Dir1", 43.0, -1.0, "D1", "A", "T");
        OpenDataIncidence dto2 = new OpenDataIncidence(2, 2, "N2", "P1", "20", "C2", "D1", LocalDateTime.now(), null,
                "R2", 0.0, 0.0, "Dir2", 43.1, -1.1, "D2", "B", "T");

        when(page1.totalPages()).thenReturn(2);
        when(page1.incidences()).thenReturn(List.of(dto1));
        when(page2.incidences()).thenReturn(List.of(dto2));

        when(apiTrafico.listaIncidenciasPorFecha(anyInt(), anyInt(), anyInt())).thenReturn(page1);
        when(apiTrafico.listaIncidencias(2)).thenReturn(page2);

        // Mocking Phase 2: resources
        Recurso recurso1 = new Recurso(1, "R1", "R1");
        when(recursoRepository.findAllById(anySet())).thenReturn(List.of(recurso1));

        OpenDataSource src2 = new OpenDataSource(2, "R2", "R2");
        when(apiTrafico.listaRecursos()).thenReturn(List.of(src2));
        when(recursoMapper.toEntity(any())).thenReturn(new Recurso(2, "R2", "R2"));

        // Mocking Phase 3: mapping
        when(incidenceMapper.toEntity(eq(dto1), any())).thenReturn(new Incidence());
        when(incidenceMapper.toEntity(eq(dto2), any())).thenReturn(new Incidence());

        // When
        List<Incidence> result = incidenceService.syncAllIncidencesFromAPI();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(incidenceRepository).saveAll(anyList());
    }

    @Test
    void shouldGetIncidencesByTipo() {
        Incidence inc = new Incidence();
        when(incidenceRepository.findAllByTipo("METEOROLOGICA")).thenReturn(List.of(inc));

        List<Incidence> result = incidenceService.getIncidences("METEOROLOGICA");

        assertEquals(1, result.size());
    }

    @Test
    void shouldSaveIncidenceSuccessfully() {
        Incidence inc = new Incidence();
        when(incidenceValidator.validar(any())).thenReturn(inc);

        Response response = incidenceService.save(inc);

        assertEquals("Incidencia guardada correctamente", response.getMensaje());
        verify(incidenceRepository).save(inc);
    }

    @Test
    void shouldReturnErrorWhenSaveFails() {
        Incidence inc = new Incidence();
        when(incidenceValidator.validar(any())).thenThrow(new RuntimeException("Validation error"));

        Response response = incidenceService.save(inc);

        assertEquals("Ha ocurrido un error al guardar la incidencia", response.getMensaje());
    }

    @Test
    void shouldLimitToMaxPaginas() {
        // Given
        OpenDataIncidenceResponse page1 = mock(OpenDataIncidenceResponse.class);
        when(page1.totalPages()).thenReturn(200); // > 100
        when(page1.incidences()).thenReturn(Collections.emptyList());
        when(apiTrafico.listaIncidenciasPorFecha(anyInt(), anyInt(), anyInt())).thenReturn(page1);

        OpenDataIncidenceResponse otherPage = mock(OpenDataIncidenceResponse.class);
        when(otherPage.incidences()).thenReturn(Collections.emptyList());
        when(apiTrafico.listaIncidencias(anyInt())).thenReturn(otherPage);

        // When
        incidenceService.syncAllIncidencesFromAPI();

        // Then
        verify(apiTrafico, times(99)).listaIncidencias(anyInt()); // 2 to 100
    }

    @Test
    void shouldHandleExceptionInValidarYMapear() {
        // Given
        OpenDataIncidenceResponse page1 = mock(OpenDataIncidenceResponse.class);
        OpenDataIncidence dto = new OpenDataIncidence(1, 1, "N", "P", "20", "C", "C", LocalDateTime.now(), null, "R",
                0.0, 0.0, "D", 43.0, -1.0, "D", "A", "T");
        when(page1.totalPages()).thenReturn(1);
        when(page1.incidences()).thenReturn(List.of(dto));
        when(apiTrafico.listaIncidenciasPorFecha(anyInt(), anyInt(), anyInt())).thenReturn(page1);

        Recurso rec = new Recurso(1, "R", "R");
        when(recursoRepository.findAllById(anySet())).thenReturn(List.of(rec));

        when(incidenceMapper.toEntity(any(), any())).thenThrow(new RuntimeException("Forced error"));

        // When
        incidenceService.syncAllIncidencesFromAPI();

        // Then
        verify(incidenceRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldGetActiveIncidences() {
        Incidence inc = new Incidence();
        inc.setEstado(Estado.ACTIVA);
        when(incidenceRepository.findAllByEstado(Estado.ACTIVA)).thenReturn(List.of(inc));
        List<Incidence> result = incidenceService.getIncidences();
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldHandleNoResourceFoundInAPI() {
        // Given
        OpenDataIncidenceResponse page1 = mock(OpenDataIncidenceResponse.class);
        OpenDataIncidence dto = new OpenDataIncidence(1, 1, "N1", "P1", "20", "C1", "D1", LocalDateTime.now(), null,
                "R1", 0.0, 0.0, "Dir1", 43.0, -1.0, "D1", "A", "T");
        when(page1.totalPages()).thenReturn(1);
        when(page1.incidences()).thenReturn(List.of(dto));
        when(apiTrafico.listaIncidenciasPorFecha(anyInt(), anyInt(), anyInt())).thenReturn(page1);

        when(recursoRepository.findAllById(anySet())).thenReturn(Collections.emptyList());
        when(apiTrafico.listaRecursos()).thenReturn(Collections.emptyList());

        // When
        List<Incidence> result = incidenceService.syncAllIncidencesFromAPI();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleUpdateExistingIncidence() {
        // Given
        OpenDataIncidenceResponse page1 = mock(OpenDataIncidenceResponse.class);
        OpenDataIncidence dto = new OpenDataIncidence(1, 1, "N1", "P1", "20", "C1", "D1", LocalDateTime.now(), null,
                "R1", 0.0, 0.0, "Dir1", 43.0, -1.0, "D1", "A", "T");
        when(page1.totalPages()).thenReturn(1);
        when(page1.incidences()).thenReturn(List.of(dto));
        when(apiTrafico.listaIncidenciasPorFecha(anyInt(), anyInt(), anyInt())).thenReturn(page1);

        Recurso rec = new Recurso(1, "R", "R");
        when(recursoRepository.findAllById(anySet())).thenReturn(List.of(rec));

        Incidence entity = new Incidence();
        entity.setId(1);
        entity.setRecurso(rec);
        when(incidenceMapper.toEntity(any(), any())).thenReturn(entity);

        Incidence existing = new Incidence();
        existing.setId(1);
        existing.setRecurso(rec);
        when(incidenceRepository.findCandidatasPorIdsExternos(anyList())).thenReturn(List.of(existing));

        // When
        incidenceService.syncAllIncidencesFromAPI();

        // Then
        verify(incidenceRepository).saveAll(anyList());
    }

    @Test
    void shouldHandleNoResourceFoundSpecialCase() {
        // Given
        OpenDataIncidenceResponse page1 = mock(OpenDataIncidenceResponse.class);
        OpenDataIncidence dto = new OpenDataIncidence(1, 1, "N1", "P1", "20", "C1", "D1", LocalDateTime.now(), null,
                "R1", 0.0, 0.0, "Dir1", 43.0, -1.0, "D1", "A", "T");
        when(page1.totalPages()).thenReturn(1);
        when(page1.incidences()).thenReturn(List.of(dto));
        when(apiTrafico.listaIncidenciasPorFecha(anyInt(), anyInt(), anyInt())).thenReturn(page1);

        when(recursoRepository.findAllById(anySet())).thenReturn(Collections.emptyList());
        when(apiTrafico.listaRecursos()).thenReturn(Collections.emptyList()); // No resource found

        // When
        List<Incidence> result = incidenceService.syncAllIncidencesFromAPI();

        // Then
        assertTrue(result.isEmpty());
    }
}
