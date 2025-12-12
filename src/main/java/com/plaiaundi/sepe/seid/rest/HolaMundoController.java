package com.plaiaundi.sepe.seid.rest;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaMundoController {

    /**
     * Peticion get 
     * @return
     */
    @GetMapping("/")
    public String saludar() {
        return "¡Hola Mundo! Spring Boot ha arrancado con éxito.";
    }

    /**
     * Forma de devolver un json
     * @return
     */
    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDatos() {
        return Map.of("estado", "ok");
    }

    /**
     * Incorporacion de datos por POST
     * @param estado
     * @param pag
     * @return
     */
    @PostMapping(value = "/solicitudes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> buscar(
        @RequestParam String estado,
        @RequestParam(defaultValue = "0") int pag 
    ) {
        return Map.of(estado, pag);
    }

    /**
     * Delete con incorporacion por ruta
     * @param id
     * @return
     */
    @DeleteMapping("/solicitudes/{id}")
    public Map<String, Object> obtenerPorId(@PathVariable Long id) {
        return Map.of("id", id);
    }
}
