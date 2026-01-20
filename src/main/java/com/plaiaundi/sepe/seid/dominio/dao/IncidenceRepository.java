package com.plaiaundi.sepe.seid.dominio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plaiaundi.sepe.seid.dominio.model.Incidence;

@Repository
public interface IncidenceRepository extends JpaRepository<Incidence, String> {

    

}
