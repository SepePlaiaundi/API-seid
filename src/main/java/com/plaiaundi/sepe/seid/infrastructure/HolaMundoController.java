package com.plaiaundi.sepe.seid.seid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaMundoController {

    @GetMapping("/")
    public String saludar() {
        return "¡Hola Mundo! Spring Boot ha arrancado con éxito.";
    }
}
