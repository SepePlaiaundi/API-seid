package com.plaiaundi.sepe.seid.infrastructure.mappers;

import com.plaiaundi.sepe.seid.dominio.model.Incidence;
import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dto.OpenDataIncidence;
import org.springframework.stereotype.Component;

@Component
public class IncidenceMapper {

    public IncidenceMapper() {
    }

    public OpenDataIncidence toDto(Incidence entity) {
        if (entity == null) {
            return null;
        }

        return new OpenDataIncidence(
                entity.getExternalId(),
                entity.getRecurso() != null ? entity.getRecurso().getId() : 0,
                null,
                entity.getProvincia(),
                null,
                entity.getCausa(),
                entity.getCiudad(),
                entity.getFecIni(),
                entity.getFecFin(),
                entity.getCarretera(),
                0.0,
                0.0,
                entity.getDireccion(),
                entity.getLatitud(),
                entity.getLongitud(),
                entity.getDescripcion(),
                entity.getNivel(),
                entity.getTipo());
    }

    public Incidence toEntity(OpenDataIncidence dto, Recurso recurso) {
        if (dto == null) {
            return null;
        }

        Incidence entity = new Incidence();
        entity.setExternalId(dto.incidenceId());
        entity.setRecurso(recurso);
        entity.setProvincia(dto.province());
        entity.setCausa(dto.cause());
        entity.setFecIni(dto.startDate());
        entity.setCarretera(dto.road());
        entity.setDireccion(dto.direction());
        entity.setLatitud(dto.latitude());
        entity.setLongitud(dto.longitude());
        entity.setCiudad(dto.cityTown());
        entity.setFecFin(dto.endDate());
        entity.setNivel(dto.incidenceLevel());
        entity.setTipo(dto.incidenceType());
        entity.setDescripcion(dto.incidenceDescription());

        return entity;
    }
}
