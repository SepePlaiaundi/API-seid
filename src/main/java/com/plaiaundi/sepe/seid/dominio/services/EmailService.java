package com.plaiaundi.sepe.seid.dominio.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ikebg@plaiaundi.net"); // dirección visible en el correo
        message.setTo(toEmail);
        message.setSubject("Bienvenido!");
        message.setText("¡Gracias por registrarte en nuestro proyecto de prueba!");

        mailSender.send(message);
    }
}
