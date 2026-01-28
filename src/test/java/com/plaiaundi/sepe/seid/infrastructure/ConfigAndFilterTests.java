package com.plaiaundi.sepe.seid.infrastructure;

import com.plaiaundi.sepe.seid.infrastructure.security.JwtAuthenticationFilter;
import com.plaiaundi.sepe.seid.infrastructure.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigAndFilterTests {

    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = Mockito.mock(JwtService.class);
        userDetailsService = Mockito.mock(UserDetailsService.class);
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testOpenDataEuskadiConfig() throws Exception {
        OpenDataEuskadiConfig config = new OpenDataEuskadiConfig();
        assertNotNull(config.restClientGenerico());
        assertNotNull(config.restClient());
    }

    @Test
    void testJwtFilterNoHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain bridge = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        // Access protected method via reflection for unit test simplicity
        Method method = JwtAuthenticationFilter.class.getDeclaredMethod("doFilterInternal",
                HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        method.invoke(filter, request, response, bridge);

        verify(bridge).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testJwtFilterWithToken() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain bridge = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(jwtService.extractEmail("valid_token")).thenReturn("test@t.com");
        when(userDetailsService.loadUserByUsername("test@t.com")).thenReturn(
                User.withUsername("test@t.com").password("p").roles("USER").build());

        Method method = JwtAuthenticationFilter.class.getDeclaredMethod("doFilterInternal",
                HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        method.invoke(filter, request, response, bridge);

        verify(bridge).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test@t.com", SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
