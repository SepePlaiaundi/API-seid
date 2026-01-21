package com.plaiaundi.sepe.seid.infrastructure.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import com.plaiaundi.sepe.seid.infrastructure.ApiTrafico;

@Component
public class CameraMapper {

    @Autowired ApiTrafico apiTrafico;
    @Autowired RecursoMapper recursoMapper; // TODO: hacer mapper
    
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
            entity.getUrlImage()
        );
    }
    
    public Camera toEntity(OpenDataCamera dto) {
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
        entity.setRecurso(      recursoMapper
            .toEntity(
                apiTrafico
                    .listaRecursos()
                    .recursos()
                    .get(dto.sourceId())
                )
            );
        entity.setUrlImage(     dto.urlImage());
        
        return entity;
    }

}
