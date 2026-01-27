package com.plaiaundi.sepe.seid.infrastructure; // OJO: Ajusta el paquete si lo tienes en .dominio.util

import com.plaiaundi.sepe.seid.dominio.model.Camera;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.proj4j.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoordinateNormalizer {

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

    public void normalize(Camera camera) {
        double latOrY = camera.getLatitud();
        double lonOrX = camera.getLongitud();

        if (latOrY == 0 && lonOrX == 0) return;

        // Si los valores son muy grandes (> 200), asumimos que son UTM
        if (Math.abs(latOrY) > 200 || Math.abs(lonOrX) > 200) {
            try {
                // En UTM: Longitud = X, Latitud = Y
                ProjCoordinate srcCoord = new ProjCoordinate(lonOrX, latOrY);
                ProjCoordinate dstCoord = new ProjCoordinate();

                transform.transform(srcCoord, dstCoord);

                // Asignamos las nuevas coordenadas convertidas
                camera.setLatitud(dstCoord.y);
                camera.setLongitud(dstCoord.x);

                log.debug("🔄 Coord convertida: UTM[{}, {}] -> GPS[{}, {}]", 
                    lonOrX, latOrY, dstCoord.x, dstCoord.y);

            } catch (Exception e) {
                log.error("❌ Error convirtiendo coordenadas: {}", e.getMessage());
            }
        }
    }
}