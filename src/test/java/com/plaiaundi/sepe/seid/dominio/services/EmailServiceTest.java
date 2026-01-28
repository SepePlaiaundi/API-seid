package com.plaiaundi.sepe.seid.dominio.services;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSendWelcomeEmail() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Welcome</html>");

        // When
        emailService.sendWelcomeEmail("test@example.com", "John");

        // Then
        verify(mailSender).send(any(MimeMessage.class));
        verify(templateEngine).process(eq("welcome-email"), any(Context.class));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenMessagingExceptionOccurs() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Welcome</html>");
        // We need to force a MessagingException.
        // MimeMessageHelper constructor or helper methods throw it.
        // We can mock some behavior to trigger it or mock MimeMessage to throw on
        // usage.
        doAnswer(inv -> {
            throw new jakarta.mail.MessagingException("Forced error");
        }).when(mailSender).send(any(MimeMessage.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> emailService.sendWelcomeEmail("test@example.com", "John"));
    }
}
