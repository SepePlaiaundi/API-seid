package com.plaiaundi.sepe.seid.rest;

import com.plaiaundi.sepe.seid.dominio.services.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/welcome")
    public ResponseEntity<Void> sendWelcomeEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "Usuario") String nombre
    ) {
        emailService.sendWelcomeEmail(email, nombre);
        return ResponseEntity.ok().build();
    }
}
