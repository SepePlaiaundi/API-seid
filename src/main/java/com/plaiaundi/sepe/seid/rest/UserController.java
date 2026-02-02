package com.plaiaundi.sepe.seid.rest;

import com.plaiaundi.sepe.seid.dominio.dao.UserRepository;
import com.plaiaundi.sepe.seid.dominio.model.User;
import com.plaiaundi.sepe.seid.dominio.services.UserService;
import com.plaiaundi.sepe.seid.dto.*;
import com.plaiaundi.sepe.seid.infrastructure.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // --- NUEVO ENDPOINT PARA OBTENER ROLES ---
    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        List<RoleResponse> roles = userService.getAllRoles().stream()
                // Asegúrate que role.getName() y role.getDescription() existan en tu entidad
                // Role
                .map(role -> new RoleResponse(role.getName(), role.getDescription()))
                .toList();
        return ResponseEntity.ok(roles);
    }
    // -----------------------------------------

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerMultipart(@ModelAttribute UserRegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginRequest request) {
        return processLogin(request);
    }

    @PostMapping(value = "/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LoginResponse> loginMultipart(@ModelAttribute UserLoginRequest request) {
        return processLogin(request);
    }

    private ResponseEntity<LoginResponse> processLogin(UserLoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            String token = jwtService.generateToken(request.email());

            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // user.getRol().getName() porque ahora 'rol' es un Objeto
            return ResponseEntity.ok(new LoginResponse(token, user.getRol().getName()));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/all")
    public List<UserResponse> list() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getEmail(),
                        user.getNombreCompleto(),
                        user.getRol().getName(),
                        user.getAvatar()))
                .toList();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> new UserResponse(
                        user.getEmail(),
                        user.getNombreCompleto(),
                        user.getRol().getName(),
                        user.getAvatar()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/update")
    public ResponseEntity<Void> updateMyProfile(@RequestBody UserProfileUpdateRequest request,
            Authentication authentication) {
        try {
            // Aseguramos que el email del request coincida con el usuario autenticado
            // o simplemente usamos el email del token para buscar el usuario.
            // En este caso, usamos el del token para mayor seguridad.
            String authenticatedEmail = authentication.getName();
            UserProfileUpdateRequest safeRequest = new UserProfileUpdateRequest(
                    authenticatedEmail,
                    request.nombreCompleto(),
                    request.password(),
                    request.avatar());

            userService.updateProfile(safeRequest);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@RequestBody UserUpdateRequest request) {
        return processUpdate(request);
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateMultipart(@ModelAttribute UserUpdateRequest request) {
        return processUpdate(request);
    }

    private ResponseEntity<Void> processUpdate(UserUpdateRequest request) { // Delegamos la lógica al servicio para
        // manejar la búsqueda del rol
        try {
            userService.updateUser(request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userService.getById(id).isPresent()) {
            userService.delete(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}