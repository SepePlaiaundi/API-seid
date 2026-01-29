package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.RecursoRepository;
import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecursoServicio {

    private final RecursoRepository recursoRepository;

    public RecursoServicio(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    public List<Recurso> getAll() {
        return recursoRepository.findAll();
    }

    public Optional<Recurso> getById(int id) {
        return recursoRepository.findById(id);
    }

    public Recurso save(Recurso recurso) {
        return recursoRepository.save(recurso);
    }

    public void delete(int id) {
        recursoRepository.deleteById(id);
    }
}
