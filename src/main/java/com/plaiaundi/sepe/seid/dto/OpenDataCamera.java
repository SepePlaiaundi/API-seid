package com.plaiaundi.sepe.seid.dto;

public record OpenDataCamera (
    // Atributos
    String address,
    String cameraId, // obligatorio
    String cameraName,
    String kilometer,
    String latitude,
    String longitude,
    String road,
    String sourceId, // obligatorio
    String urlImage
) {}