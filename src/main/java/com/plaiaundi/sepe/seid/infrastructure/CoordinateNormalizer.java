package com.plaiaundi.sepe.seid.infrastructure; // OJO: Ajusta el paquete si lo tienes en .dominio.util

import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.model.Incidence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.locationtech.proj4j.*;
import org.springframework.stereotype.Component;

@Component
public class CoordinateNormalizer {

    private static final Logger log = LoggerFactory.getLogger(CoordinateNormalizer.class);

    private final CoordinateTransform transform;

    public CoordinateNormalizer() {
        CRSFactory crsFactory = new CRSFactory();

        // 1. Definimos ETRS89 / UTM zone 30N (EPSG:25830) - País Vasco
        CoordinateReferenceSystem sourceCRS = crsFactory.createFromParameters("EPSG:25830",
                "+proj=utm +zone=30 +ellps=GRS80 +units=m +no_defs");

        // 2. Definimos WGS84 (EPSG:4326) - GPS Estándar
        CoordinateReferenceSystem targetCRS = crsFactory.createFromParameters("EPSG:4326",
                "+proj=longlat +datum=WGS84 +no_defs");

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        this.transform = ctFactory.createTransform(sourceCRS, targetCRS);
    }

    public void normalize(Incidence incidence) {
        double[] coord;
        try {
            if (incidence.getRecurso() != null && incidence.getRecurso().getId() == 2) { // Caso de bizkaia que vienen
                                                                                         // invertidos.
                coord = normalize(incidence.getLongitud(), incidence.getLatitud());
            } else {
                coord = normalize(incidence.getLatitud(), incidence.getLongitud());
            }
            incidence.setLatitud(coord[0]);
            incidence.setLongitud(coord[1]);
        } catch (Exception e) {
            log.error("❌ Error normalizando coordenadas de incidencia: {}", e.getMessage());
        }

    }

    public void normalize(Camera camera) {
        double[] coord;
        try {
            if (camera.getRecurso() != null && camera.getRecurso().getId() == 2) { // Caso de bizkaia que vienen
                                                                                   // invertidos.
                coord = normalize(camera.getLongitud(), camera.getLatitud());
            } else {
                coord = normalize(camera.getLatitud(), camera.getLongitud());
            }
            camera.setLatitud(coord[0]);
            camera.setLongitud(coord[1]);
        } catch (Exception e) {
            log.error("❌ Error normalizando coordenadas de cámara: {}", e.getMessage());
        }

    }

    private double[] normalize(double latitud, double longitud) throws Exception {
        double latOrY = latitud;
        double lonOrX = longitud;

        if (latOrY == 0 && lonOrX == 0)
            throw new Exception("Los valores no pueden ser 0");

        // Si los valores son muy grandes (> 200), asumimos que son UTM
        if (Math.abs(latOrY) > 200 || Math.abs(lonOrX) > 200) {
            try {
                // En UTM: Longitud = X, Latitud = Y
                ProjCoordinate srcCoord = new ProjCoordinate(lonOrX, latOrY);
                ProjCoordinate dstCoord = new ProjCoordinate();

                transform.transform(srcCoord, dstCoord);
                log.debug("🔄 Coord convertida: UTM[{}, {}] -> GPS[{}, {}]", lonOrX, latOrY, dstCoord.x, dstCoord.y);

                return new double[] { dstCoord.x, dstCoord.y };

            } catch (Exception e) {
                log.error("❌ Error convirtiendo coordenadas: {}", e.getMessage());
            }
        }

        return new double[] { latOrY, lonOrX };
    }

}