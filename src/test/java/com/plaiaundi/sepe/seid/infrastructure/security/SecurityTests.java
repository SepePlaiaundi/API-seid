package com.plaiaundi.sepe.seid.infrastructure.security;

import com.plaiaundi.sepe.seid.dominio.dao.UserRepository;
import com.plaiaundi.sepe.seid.dominio.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SecurityTests {

    private JwtService jwtService;
    private UserRepository userRepository;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        userRepository = Mockito.mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void testJwtService() {
        String email = "test@example.com";
        String token = jwtService.generateToken(email);
        assertNotNull(token);

        String extracted = jwtService.extractEmail(token);
        assertEquals(email, extracted);
    }

    @Test
    void testUserDetailsServiceSuccess() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("hashed_pass");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername(email);
        assertEquals(email, details.getUsername());
        assertEquals("hashed_pass", details.getPassword());
    }

    @Test
    void testUserDetailsServiceNotFound() {
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }
}
