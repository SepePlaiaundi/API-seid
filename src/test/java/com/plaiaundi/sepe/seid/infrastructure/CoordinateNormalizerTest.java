package com.plaiaundi.sepe.seid.infrastructure;

import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.model.Incidence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateNormalizerTest {

    private CoordinateNormalizer normalizer;

    @BeforeEach
    void setUp() {
        normalizer = new CoordinateNormalizer();
    }

    @Test
    void shouldTransformUTMToGPSForIncidence() {
        // Given: Coordinates in UTM zone 30N (Euskadi)
        // Example: X=500000, Y=4800000 approx near Bilbao/Vitoria area
        // Let's use more realistic ones if possible, but 500000/4800000 is a good UTM
        // test
        Incidence incidence = new Incidence();
        incidence.setLatitud(4800000.0); // Y
        incidence.setLongitud(500000.0); // X

        // When
        normalizer.normalize(incidence);

        // Then: Values should be significantly smaller (around 43 degrees Lat, -3
        // degrees Lon)
        assertTrue(Math.abs(incidence.getLatitud()) < 100, "Latitude should be GPS format");
        assertTrue(Math.abs(incidence.getLongitud()) < 100, "Longitude should be GPS format");

        logCoordinates(incidence.getLatitud(), incidence.getLongitud());
    }

    @Test
    void shouldHandleExceptionInIncidenceNormalization() {
        Incidence incidence = new Incidence() {
            @Override
            public double getLatitud() {
                throw new RuntimeException("Forced error");
            }
        };
        assertDoesNotThrow(() -> normalizer.normalize(incidence));
    }

    @Test
    void shouldTransformUTMToGPSForCamera() {
        Camera camera = new Camera();
        camera.setLatitud(4795000.0);
        camera.setLongitud(505000.0);

        normalizer.normalize(camera);

        assertTrue(Math.abs(camera.getLatitud()) < 100);
        assertTrue(Math.abs(camera.getLongitud()) < 100);
    }

    @Test
    void shouldNotTransformStandardGPSCoordinates() {
        // Given: Standard GPS coords (Gipuzkoa/Plaiaundi area approx)
        double lat = 43.33;
        double lon = -1.78;
        Camera camera = new Camera();
        camera.setLatitud(lat);
        camera.setLongitud(lon);

        // When
        normalizer.normalize(camera);

        // Then: Should remain unchanged
        assertEquals(lat, camera.getLatitud(), 0.0001);
        assertEquals(lon, camera.getLongitud(), 0.0001);
    }

    @Test
    void shouldHandlePartialUTMCoordinates() {
        Camera camera = new Camera();
        camera.setLatitud(4795000.0); // Y > 200
        camera.setLongitud(43.0); // X < 200
        normalizer.normalize(camera);
        assertTrue(Math.abs(camera.getLatitud()) < 100);

        Camera camera2 = new Camera();
        camera2.setLatitud(43.0); // Y < 200
        camera2.setLongitud(505000.0); // X > 200
        normalizer.normalize(camera2);
        assertTrue(Math.abs(camera2.getLongitud()) < 100);
    }

    @Test
    void shouldHandleZeroCoordinates() {
        Camera camera = new Camera();
        camera.setLatitud(0.0);
        camera.setLongitud(0.0);

        assertDoesNotThrow(() -> normalizer.normalize(camera));
        assertEquals(0.0, camera.getLatitud());
    }

    @Test
    void shouldHandleExceptionInNormalization() {
        // Since transform is final and crsFactory is local, it's hard to force an
        // exception
        // but we can try with invalid coordinates that might cause issues if Proj4j
        // fails
        Camera camera = new Camera();
        camera.setLatitud(Double.NaN);
        camera.setLongitud(500000.0);
        assertDoesNotThrow(() -> normalizer.normalize(camera));
    }

    private void logCoordinates(double lat, double lon) {
        System.out.println("Converted Coordinates: Lat=" + lat + ", Lon=" + lon);
    }
}
