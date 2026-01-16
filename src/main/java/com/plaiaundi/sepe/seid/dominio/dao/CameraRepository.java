package com.plaiaundi.sepe.seid.dominio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plaiaundi.sepe.seid.dominio.model.Camera;

import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Integer> {

    List<Camera> findByLatitudAndLongitud(String latitud, String longitud);
    Camera findById(String id);

}
