package com.plaiaundi.sepe.seid.dominio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.model.Estado;

import java.util.List;
import java.util.Optional;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Integer> {

    Optional<Camera> findById(String id);

    List<Camera> findAllByEstado(Estado estado);

    @Query("SELECT c FROM Camera c WHERE c.externalId IN :ids")
    List<Camera> findCandidatasPorIdsExternos(@Param("ids") List<Integer> ids);

    @Query("SELECT c.id FROM Camera c WHERE c.id IN :ids")
    List<Integer> findExistingIds(@Param("ids") List<Integer> ids);
}
