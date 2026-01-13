package com.plaiaundi.sepe.seid.dto;

import jakarta.persistence.Id;

public record OpenDataCamera (
    // Atributos
    String address,
    @Id
    String cameraId, // obligatorio
    String cameraName,
    String kilometer,
    String latitude,
    String longitude,
    String road,
    String sourceId, // obligatorio
    String urlImage
) {}