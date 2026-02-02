package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.RoleRepository;
import com.plaiaundi.sepe.seid.dominio.dao.UserRepository;
import com.plaiaundi.sepe.seid.dominio.model.Role;
import com.plaiaundi.sepe.seid.dominio.model.User;
import com.plaiaundi.sepe.seid.dto.UserRegisterRequest;
import com.plaiaundi.sepe.seid.dto.UserUpdateRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // Inyectamos el repo de roles
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public void register(UserRegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }

        User user = new User();
        user.setNombreCompleto(request.nombreCompleto());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        // Buscamos el rol en la BD. Si no envían nada, asignamos USER por defecto.
        String roleName = (request.rol() != null && !request.rol().isEmpty()) ? request.rol() : "USER";

        Role roleEntity = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));

        user.setRol(roleEntity);

        userRepository.save(user);
    }

    // Método nuevo para actualizar usuario con Rol
    public void updateUser(UserUpdateRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setNombreCompleto(request.nombreCompleto());

        // Actualizar rol si viene en la petición
        if (request.rol() != null) {
            Role roleEntity = roleRepository.findByName(request.rol())
                    .orElseThrow(() -> new RuntimeException("Rol no válido"));
            user.setRol(roleEntity);
        }

        userRepository.save(user);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}