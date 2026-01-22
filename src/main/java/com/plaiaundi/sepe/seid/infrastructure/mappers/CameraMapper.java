package com.plaiaundi.sepe.seid.infrastructure.mappers;

import com.plaiaundi.sepe.seid.dominio.util.CameraValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import com.plaiaundi.sepe.seid.infrastructure.ApiTrafico;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Component
public class CameraMapper {

    @Autowired ApiTrafico apiTrafico;
    @Autowired RecursoMapper recursoMapper;
    @Autowired CameraValidator cameraValidator;
    
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
    
    public Camera toEntity(OpenDataCamera dto) throws MalformedURLException {
        if (dto == null) {
            return null;
        }

        Camera entity = new Camera();
        entity.setDireccion(    dto.address());
        entity.setId(           dto.cameraId());
        entity.setNombre(       dto.cameraName());
        entity.setKilometro(    dto.kilometer());
        entity.setLatitud(      dto.latitude());
        entity.setLongitud(     dto.longitude());
        entity.setCarretera(    dto.road());
        entity.setRecurso(
            recursoMapper
                .toEntity(
                    apiTrafico
                        .listaRecursos()
                        .get(dto.sourceId())
                )
        );
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
