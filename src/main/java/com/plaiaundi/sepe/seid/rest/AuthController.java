package com.plaiaundi.sepe.seid.rest;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.plaiaundi.sepe.seid.dominio.model.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name="Autentificacion", description = "Endpoints para la autentificacion de los demas endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión de usuario", // <--- 2. Nombre Human-First
        description = "Envía usuario y contraseña para recibir un token Bearer JWT válido por 24h." // <--- Comentario largo
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso, devuelve el token"),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas"),
        @ApiResponse(responseCode = "403", description = "Usuario bloqueado o sin permisos")
    })
    // Al poner @RequestBody AuthRequest request, Spring espera un JSON con los campos de esa clase
    public ResponseEntity<?> login(@RequestBody String usuario, @RequestBody String contrasena) { 
        try {
            AuthRequest request = new AuthRequest(usuario, contrasena);
            // Validamos usando los getters del objeto request
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String token = jwtService.generateToken(authentication.getName());
            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("error", "Usuario o contraseña incorrectos"));
        }
    }
}