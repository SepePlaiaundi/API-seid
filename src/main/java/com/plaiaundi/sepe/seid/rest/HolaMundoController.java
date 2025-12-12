package com.plaiaundi.sepe.seid.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@RestController
public class HolaMundoController {

    @JsonPropertyOrder({ "id", "nombre", "rol" })
    public static class Usuario {
        private String id;
        private String nombre;
        private String rol;

        public Usuario() {}
        public Usuario(String id, String nombre, String rol) {
            this.id = id;
            this.nombre = nombre;
            this.rol = rol;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getNombre() {
            return nombre;
        }
        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
        public String getRol() {
            return rol;
        }
        public void setRol(String rol) {
            this.rol = rol;
        }
    }

    /**
     * Prueba de devolucion de multiples formatos
     * gracias a las dependencias usadas y a las etiquetas si se 
     * marca el accept con application/json, application/xml o text/csv
     * devuelve el formato en el el pedido de cara a ser usado mas facilmente
     * @return
     */
    @GetMapping("/usuarios")
    public List<Usuario> obtenerUsuario() {
        return List.of(
            new Usuario("1", "Ana Garcia", "Admin"),
            new Usuario("2", "Beto Perez", "User"),
            new Usuario("3", "Carla Ruiz", "Editor")
        );
    }

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
