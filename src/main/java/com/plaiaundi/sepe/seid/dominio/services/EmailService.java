package com.plaiaundi.sepe.seid.dominio.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine; // O spring5 según tu versión

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendWelcomeEmail(String toEmail, String nombreUsuario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("nombre", nombreUsuario);

            String html = templateEngine.process("welcome-email", context);

            helper.setTo(toEmail);
            helper.setText(html, true);
            helper.setSubject("¡Bienvenido a TrafficMap! 🚦");
            helper.setFrom("ikebg@plaiaundi.net");

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar email de bienvenida", e);
        }
    }
}