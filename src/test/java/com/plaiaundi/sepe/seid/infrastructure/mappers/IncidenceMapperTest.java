package com.plaiaundi.sepe.seid.infrastructure.mappers;

import com.plaiaundi.sepe.seid.dominio.model.Incidence;
import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IncidenceMapperTest {

    private IncidenceMapper incidenceMapper;

    @BeforeEach
    void setUp() {
        incidenceMapper = new IncidenceMapper();
    }

    @Test
    void shouldReturnNullWhenMappingNullEntity() {
        assertNull(incidenceMapper.toDto(null));
    }

    @Test
    void shouldMapEntityToDto() {
        // Given
        Recurso recurso = new Recurso(1, "Rec", "Rec");
        Incidence incidence = new Incidence();
        incidence.setId(505);
        incidence.setProvincia("Gipuzkoa");
        incidence.setRecurso(recurso);
        incidence.setCausa("OBRA");
        incidence.setCiudad("Donostia");
        incidence.setFecIni(LocalDateTime.now());
        incidence.setFecFin(LocalDateTime.now().plusHours(2));
        incidence.setCarretera("N-1");
        incidence.setDireccion("Dir 1");
        incidence.setLatitud(1.0);
        incidence.setLongitud(2.0);
        incidence.setDescripcion("Desc");
        incidence.setNivel("NEGRO");
        incidence.setTipo("METEOROLOGICA");

        // When
        OpenDataIncidence dto = incidenceMapper.toDto(incidence);

        // Then
        assertNotNull(dto);
        assertEquals(505, dto.incidenceId());
        assertEquals(1, dto.sourceId());
        assertEquals("Gipuzkoa", dto.province());
        assertEquals("OBRA", dto.cause());
    }

    @Test
    void shouldMapEntityWithNullRecursoToDto() {
        // Given
        Incidence incidence = new Incidence();
        incidence.setId(505);

        // When
        OpenDataIncidence dto = incidenceMapper.toDto(incidence);

        // Then
        assertNotNull(dto);
        assertEquals(0, dto.sourceId());
    }

    @Test
    void shouldMapDtoToEntity() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        OpenDataIncidence dto = new OpenDataIncidence(505, 1, "2024", "Gipuzkoa", "Cause", "OBRA", "Donostia", now,
                now.plusHours(1), "N-1", 1.0, 2.0, "Dir", 1.0, 2.0, "Desc", "NEGRO", "METEO");
        Recurso recurso = new Recurso(1, "Rec", "Rec");

        // When
        Incidence entity = incidenceMapper.toEntity(dto, recurso);

        // Then
        assertNotNull(entity);
        assertEquals(505, entity.getId());
        assertEquals(recurso, entity.getRecurso());
        assertEquals("Gipuzkoa", entity.getProvincia());
        assertEquals("OBRA", entity.getCausa());
    }

    @Test
    void shouldReturnNullWhenMappingNullDto() {
        assertNull(incidenceMapper.toEntity(null, null));
    }
}
