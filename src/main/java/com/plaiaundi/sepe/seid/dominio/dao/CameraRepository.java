package com.plaiaundi.sepe.seid.dominio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plaiaundi.sepe.seid.dominio.model.Camera;
import com.plaiaundi.sepe.seid.dominio.model.Camera.Estado;

import java.util.List;
import java.util.Optional;

@Repository
public interface CameraRepository extends JpaRepository<Camera, String> {

    List<Camera> findByLatitudAndLongitud(String latitud, String longitud);
    Optional<Camera> findById(String id);
    List<Camera> findAllByEstado(Estado estado);
}
