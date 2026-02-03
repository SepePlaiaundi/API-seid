package com.plaiaundi.sepe.seid.dto;

public record UserProfileUpdateRequest(
        String email,
        String nombreCompleto,
        String password,
        String avatar
) {}
