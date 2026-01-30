package com.plaiaundi.sepe.seid.rest;

import com.plaiaundi.sepe.seid.dominio.dao.UserRepository;
import com.plaiaundi.sepe.seid.dominio.model.User;
import com.plaiaundi.sepe.seid.dominio.services.UserService;
import com.plaiaundi.sepe.seid.dto.*;
import com.plaiaundi.sepe.seid.infrastructure.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
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
                // Asegúrate que role.getName() y role.getDescription() existan en tu entidad Role
                .map(role -> new RoleResponse(role.getName(), role.getDescription()))
                .toList();
        return ResponseEntity.ok(roles);
    }
    // -----------------------------------------

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

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
                        user.getRol().getName() // Enviamos el código del rol (ej: "ADMIN")
                ))
                .toList();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody UserUpdateRequest request) {
        // Delegamos la lógica al servicio para manejar la búsqueda del rol
        try {
            userService.updateUser(request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}