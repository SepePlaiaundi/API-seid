package com.plaiaundi.sepe.seid.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Muestra la pantalla de Login
    @GetMapping("/login")
    public String login() {
        return "login"; // Busca login.html en templates
    }

    // Página de destino tras loguearse (ejemplo)
    @GetMapping("/home")
    public String home() {
        return "home"; // Busca home.html en templates
    }

    @GetMapping("/")
    public String root() {
        // "redirect:" le dice al navegador que cambie de URL
        return "redirect:/swagger-ui/index.html";
    }
}

