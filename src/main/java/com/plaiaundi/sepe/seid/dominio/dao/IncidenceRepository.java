package com.plaiaundi.sepe.seid.dominio.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.model.Estado;
import com.plaiaundi.sepe.seid.dominio.model.Incidence;

@Repository
public interface IncidenceRepository extends JpaRepository<Incidence, Integer> {

    List<Incidence> findAllByEstado(Estado activa);

    @Query("SELECT i FROM Incidence i WHERE i.id IN :ids")
    List<Incidence> findCandidatasPorIdsExternos(@Param("ids") List<Integer> idsExternos);

    List<Incidence> findAllByTipo(String tipo);

    List<Incidence> findAllByEstadoAndUltimaActualizacionAfter(Estado estado, LocalDateTime since);

}
