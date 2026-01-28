package com.plaiaundi.sepe.seid.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OpenDataCameraTest {

    @Test
    void testConstructorAndGetters() {
        OpenDataCamera dto = new OpenDataCamera(
                "Address", 101, "Name", "10.5", 43.0, -1.0, "Road", 1, "url");

        assertEquals("Address", dto.address());
        assertEquals(101, dto.cameraId());
        assertEquals("Name", dto.cameraName());
        assertEquals("10.5", dto.kilometer());
        assertEquals(43.0, dto.latitude());
        assertEquals(-1.0, dto.longitude());
        assertEquals("Road", dto.road());
        assertEquals(1, dto.sourceId());
        assertEquals("url", dto.urlImage());
    }
}
