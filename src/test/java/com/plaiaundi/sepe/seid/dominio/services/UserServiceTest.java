package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.UserRepository;
import com.plaiaundi.sepe.seid.dominio.model.User;
import com.plaiaundi.sepe.seid.dto.UserRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest("John Doe", "john@example.com", "password123");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        userService.register(request);

        // Then
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest("John Doe", "john@example.com", "password123");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(request));
        assertEquals("Email ya registrado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
