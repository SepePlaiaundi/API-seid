package com.plaiaundi.sepe.seid.dominio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;

    @Column(nullable = false)
    private String email;

    @JsonIgnore
    private String password;

    // CAMBIO IMPORTANTE: Relación ManyToOne
    // Un usuario tiene un rol, pero un rol tiene muchos usuarios.
    @ManyToOne(fetch = FetchType.EAGER) // Eager para cargar el rol al pedir el usuario
    @JoinColumn(name = "role_id")
    private Role rol;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    // Getters y Setters actualizados
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRol() {
        return rol;
    }

    public void setRol(Role rol) {
        this.rol = rol;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}