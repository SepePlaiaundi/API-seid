package com.plaiaundi.sepe.seid.infrastructure.mappers;

import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dto.OpenDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecursoMapperTest {

    private RecursoMapper recursoMapper;

    @BeforeEach
    void setUp() {
        recursoMapper = new RecursoMapper();
    }

    @Test
    void shouldReturnNullWhenMappingNullEntity() {
        assertNull(recursoMapper.toDto(null));
    }

    @Test
    void shouldMapEntityToDto() {
        // Given
        Recurso entity = new Recurso(1, "Desc ES", "Desc EU");

        // When
        OpenDataSource dto = recursoMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.id());
        assertEquals("Desc ES", dto.descripcionEs());
        assertEquals("Desc EU", dto.descripcionEu());
    }

    @Test
    void shouldReturnNullWhenMappingNullDto() {
        assertNull(recursoMapper.toEntity(null));
    }

    @Test
    void shouldMapDtoToEntity() {
        // Given
        OpenDataSource dto = new OpenDataSource(1, "Desc ES", "Desc EU");

        // When
        Recurso entity = recursoMapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(1, entity.getId());
        assertEquals("Desc ES", entity.getDescEs());
        assertEquals("Desc EU", entity.getDescEu());
    }
}
