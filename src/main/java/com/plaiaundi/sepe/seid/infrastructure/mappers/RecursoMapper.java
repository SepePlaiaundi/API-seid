package com.plaiaundi.sepe.seid.infrastructure.mappers;

import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dto.OpenDataCamera;
import com.plaiaundi.sepe.seid.dto.OpenDataSource;
import com.plaiaundi.sepe.seid.infrastructure.ApiTrafico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecursoMapper {

    public OpenDataSource toDto(Recurso entity) {
        if (entity == null) {
            return null;
        }

        return new OpenDataSource(
            entity.getId(),
            entity.getDescEs(),
            entity.getDescEu()
        );
    }
    
    public Recurso toEntity(OpenDataSource dto) {
        if (dto == null) {
            return null;
        }

        Recurso entity = new Recurso();
        entity.setId(        dto.id());
        entity.setDescEs(    dto.descripcionEs());
        entity.setDescEu(    dto.descripcionEu());
        
        return entity;
    }

}
