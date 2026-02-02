package com.plaiaundi.sepe.seid.dto;

public record UserRegisterRequest(
        String nombreCompleto,
        String email,
        String password,
        String rol
) {}