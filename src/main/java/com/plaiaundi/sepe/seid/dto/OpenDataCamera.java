package com.plaiaundi.sepe.seid.dto;

import java.net.URL;

import jakarta.validation.constraints.NotEmpty;

public record OpenDataCamera (
    // Atributos    
    String address,
    @NotEmpty
    int cameraId, // obligatorio
    String cameraName,
    String kilometer,
    double latitude,
    double longitude,
    String road,
    @NotEmpty
    int sourceId, // obligatorio
    String urlImage
) {}