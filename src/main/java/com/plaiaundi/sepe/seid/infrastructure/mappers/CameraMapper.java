package com.plaiaundi.sepe.seid.infrastructure.mappers;

import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dominio.util.CameraValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;

import java.net.MalformedURLException;
import java.net.URI;

@Component
public class CameraMapper {

    @Autowired
    private CameraValidator cameraValidator;

    public OpenDataCamera toDto(Camera entity) {
        if (entity == null) {
            return null;
        }

        return new OpenDataCamera(
            entity.getDireccion(),
            entity.getId(),
            entity.getNombre(),
            entity.getKilometro(),
            entity.getLatitud(),
            entity.getLongitud(),
            entity.getCarretera(),
            entity.getRecurso().getId(),
            entity.getUrlImage().toString()
        );
    }

    /**
     * Convierte un DTO de cámara a una entidad Camera.
     * Este método ahora requiere que el Recurso ya esté resuelto y se pase como argumento.
     *
     * @param dto El objeto de transferencia de datos de la cámara.
     * @param recurso La entidad Recurso ya gestionada que se asociará a la cámara.
     * @return La entidad Camera mapeada.
     * @throws MalformedURLException Si la URL de la imagen no es válida.
     */
    public Camera toEntity(OpenDataCamera dto, Recurso recurso) throws MalformedURLException {
        if (dto == null) {
            return null;
        }

        Camera entity = new Camera();
        entity.setDireccion(dto.address());
        entity.setId(dto.cameraId());
        entity.setNombre(dto.cameraName());
        entity.setKilometro(dto.kilometer());
        entity.setLatitud(dto.latitude());
        entity.setLongitud(dto.longitude());
        entity.setCarretera(dto.road());
        entity.setRecurso(recurso); // Asignación directa del recurso ya resuelto
        entity.setUrlImage(
            URI
                .create(
                    cameraValidator.limpiarYTransformarUrl(dto.urlImage())
                )
                .toURL()
        );

        return entity;
    }
}
