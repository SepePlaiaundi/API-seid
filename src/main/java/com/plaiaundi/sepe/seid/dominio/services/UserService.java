package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.RoleRepository;
import com.plaiaundi.sepe.seid.dominio.dao.UserRepository;
import com.plaiaundi.sepe.seid.dominio.model.Role;
import com.plaiaundi.sepe.seid.dominio.model.User;
import com.plaiaundi.sepe.seid.dto.UserProfileUpdateRequest;
import com.plaiaundi.sepe.seid.dto.UserRegisterRequest;
import com.plaiaundi.sepe.seid.dto.UserUpdateRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // 1. Añadir el servicio de email

    // 2. Inyectarlo en el constructor
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void register(UserRegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }

        User user = new User();
        user.setNombreCompleto(request.nombreCompleto());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        String roleName = (request.rol() != null && !request.rol().isEmpty()) ? request.rol() : "USER";
        Role roleEntity = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));

        user.setRol(roleEntity);

        // Guardamos el usuario
        userRepository.save(user);

        // Si bypassEmail es true, no enviamos el correo
        if (request.bypassEmail() != null && request.bypassEmail()) {
            logger.info("Registro de usuario: bypassEmail activo para {}, saltando correo de bienvenida.", user.getEmail());
            return;
        }

        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getNombreCompleto());
        } catch (Exception e) {
            logger.error("Usuario registrado, pero falló el envío del email: " + e.getMessage());
        }
    }

    // ... Resto de métodos (getAll, getById, update, etc.) sin cambios ...
    public List<User> getAll() { return userRepository.findAll(); }
    public Optional<User> getById(Long id) { return userRepository.findById(id); }
    public User save(User user) { return userRepository.save(user); }
    public void delete(Long id) { userRepository.deleteById(id); }

    public void updateUser(UserUpdateRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setNombreCompleto(request.nombreCompleto());
        if (request.rol() != null) {
            Role roleEntity = roleRepository.findByName(request.rol())
                    .orElseThrow(() -> new RuntimeException("Rol no válido"));
            user.setRol(roleEntity);
        }
        userRepository.save(user);
    }

    public void updateProfile(UserProfileUpdateRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setNombreCompleto(request.nombreCompleto());
        user.setAvatar(request.avatar());
        if (request.password() != null && !request.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        userRepository.save(user);
    }

    public List<Role> getAllRoles() { return roleRepository.findAll(); }
}