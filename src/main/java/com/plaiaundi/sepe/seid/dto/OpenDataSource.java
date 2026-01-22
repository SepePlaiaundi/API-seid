package com.plaiaundi.sepe.seid.dto;

import jakarta.persistence.Id;

public record OpenDataSource(
    @Id
    int id,
    String descripcionEs,
    String descripcionEu
) {}
