package com.plaiaundi.sepe.seid.rest;

import com.plaiaundi.sepe.seid.dominio.model.Recurso;
import com.plaiaundi.sepe.seid.dominio.services.RecursoServicio;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recurso")
@CrossOrigin(origins = "*")
public class RecursoController {

    private final RecursoServicio recursoServicio;

    public RecursoController(RecursoServicio recursoServicio) {
        this.recursoServicio = recursoServicio;
    }

    @GetMapping
    public List<Recurso> getAll() {
        return recursoServicio.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recurso> getById(@PathVariable int id) {
        return recursoServicio.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Recurso create(@RequestBody Recurso recurso) {
        return recursoServicio.save(recurso);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Recurso createMultipart(@ModelAttribute Recurso recurso) {
        return recursoServicio.save(recurso);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recurso> update(@PathVariable int id, @RequestBody Recurso recurso) {
        return processUpdate(id, recurso);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Recurso> updateMultipart(@PathVariable int id, @ModelAttribute Recurso recurso) {
        return processUpdate(id, recurso);
    }

    private ResponseEntity<Recurso> processUpdate(int id, Recurso recurso) {
        return recursoServicio.getById(id)
                .map(existing -> {
                    recurso.setId(id);
                    return ResponseEntity.ok(recursoServicio.save(recurso));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (recursoServicio.getById(id).isPresent()) {
            recursoServicio.delete(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
