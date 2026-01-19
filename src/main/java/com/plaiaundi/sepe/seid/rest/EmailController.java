package com.plaiaundi.sepe.seid.rest;
import com.plaiaundi.sepe.seid.dominio.services.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-test-email")
    public String sendEmail(@RequestParam String email) {
        emailService.sendWelcomeEmail(email);
        return "Email enviado a " + email;
    }
}
