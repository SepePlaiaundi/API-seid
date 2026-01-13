package com.plaiaundi.sepe.seid.dominio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plaiaundi.sepe.seid.dominio.model.Camara;

@Repository
public interface CamaraRepository extends JpaRepository<Camara, Integer> {

}
