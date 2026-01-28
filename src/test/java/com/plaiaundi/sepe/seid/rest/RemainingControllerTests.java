package com.plaiaundi.sepe.seid.rest;

import com.plaiaundi.sepe.seid.dominio.model.Bookmark;
import com.plaiaundi.sepe.seid.dominio.services.BookmarkService;
import com.plaiaundi.sepe.seid.dominio.services.EmailService;
import com.plaiaundi.sepe.seid.dominio.services.UserService;
import com.plaiaundi.sepe.seid.dto.AddBookmarkRequest;
import com.plaiaundi.sepe.seid.dto.UserLoginRequest;
import com.plaiaundi.sepe.seid.dto.UserRegisterRequest;
import com.plaiaundi.sepe.seid.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RemainingControllerTests {

    private MockMvc mockEmail;
    private MockMvc mockBookmark;
    private MockMvc mockUser;

    private EmailService emailService;
    private BookmarkService bookmarkService;
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        emailService = Mockito.mock(EmailService.class);
        bookmarkService = Mockito.mock(BookmarkService.class);
        userService = Mockito.mock(UserService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtService = Mockito.mock(JwtService.class);

        mockEmail = MockMvcBuilders.standaloneSetup(new EmailController(emailService)).build();
        mockBookmark = MockMvcBuilders.standaloneSetup(new BookmarkController(bookmarkService))
                .setCustomArgumentResolvers(new MockPrincipalArgumentResolver())
                .build();
        mockUser = MockMvcBuilders.standaloneSetup(new UserController(userService, authenticationManager, jwtService))
                .build();
    }

    private static class MockPrincipalArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter,
                ModelAndViewContainer mavContainer,
                NativeWebRequest webRequest,
                WebDataBinderFactory binderFactory) {
            UserDetails userDetails = User.withUsername("test@t.com")
                    .password("pass")
                    .roles("USER")
                    .build();
            return userDetails;
        }
    }

    @Test
    void testEmailWelcome() throws Exception {
        mockEmail.perform(post("/emails/welcome")
                .param("email", "test@test.com")
                .param("nombre", "John"))
                .andExpect(status().isOk());
    }

    @Test
    void testBookmarkAdd() throws Exception {
        mockBookmark.perform(post("/bookmarks/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cameraId\": \"CAM1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testBookmarkDelete() throws Exception {
        mockBookmark.perform(delete("/bookmarks/CAM1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testBookmarkList() throws Exception {
        when(bookmarkService.getBookmarks(any())).thenReturn(Collections.emptyList());
        mockBookmark.perform(get("/bookmarks"))
                .andExpect(status().isOk());
    }

    @Test
    void testUserRegister() throws Exception {
        mockUser.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombreCompleto\": \"John\", \"email\": \"test@t.com\", \"password\": \"123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUserLoginSuccess() throws Exception {
        when(jwtService.generateToken(anyString())).thenReturn("token");
        mockUser.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"test@t.com\", \"password\": \"123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUserLoginFailure() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad"));
        mockUser.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"test@t.com\", \"password\": \"123\"}"))
                .andExpect(status().isUnauthorized());
    }
}
