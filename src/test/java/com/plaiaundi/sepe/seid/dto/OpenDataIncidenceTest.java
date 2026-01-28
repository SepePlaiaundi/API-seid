package com.plaiaundi.sepe.seid.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class OpenDataIncidenceTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        OpenDataIncidence dto = new OpenDataIncidence(
                1, 2, "2024", "Prov", "Cause", "Text", "City",
                now, now.plusHours(1), "Road", 1.0, 2.0, "Dir",
                3.0, 4.0, "Desc", "Level", "Type");

        assertEquals(1, dto.incidenceId());
        assertEquals(2, dto.sourceId());
        assertEquals("Prov", dto.province());
        assertEquals(now, dto.startDate());
        assertEquals(3.0, dto.latitude());
        assertEquals("Type", dto.incidenceType());
    }
}
