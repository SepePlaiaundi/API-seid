package com.plaiaundi.sepe.seid.infrastructure.mappers;

import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dominio.util.CameraValidator;
import org.springframework.stereotype.Component;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;

import java.net.MalformedURLException;
import java.net.URI;

@Component
public class CameraMapper {

    private final CameraValidator cameraValidator;

    public CameraMapper(CameraValidator cameraValidator) {
        this.cameraValidator = cameraValidator;
    }

    public OpenDataCamera toDto(Camera entity) {
        if (entity == null) {
            return null;
        }

        return new OpenDataCamera(
                entity.getDireccion(),
                entity.getExternalId(),
                entity.getNombre(),
                entity.getKilometro(),
                entity.getLatitud(),
                entity.getLongitud(),
                entity.getCarretera(),
                entity.getRecurso() != null ? entity.getRecurso().getId() : 0,
                entity.getUrlImage() != null ? entity.getUrlImage().toString() : null);
    }

    public Camera toEntity(OpenDataCamera dto, Recurso recurso) throws MalformedURLException {
        if (dto == null) {
            return null;
        }

        Camera entity = new Camera();
        entity.setDireccion(dto.address());
        entity.setExternalId(dto.cameraId());
        entity.setNombre(dto.cameraName());
        entity.setKilometro(dto.kilometer());
        entity.setLatitud(dto.latitude());
        entity.setLongitud(dto.longitude());
        entity.setCarretera(dto.road());
        entity.setRecurso(recurso);

        String cleanUrl = cameraValidator.limpiarYTransformarUrl(dto.urlImage());
        if (cleanUrl != null) {
            entity.setUrlImage(URI.create(cleanUrl).toURL());
        }

        return entity;
    }
}
