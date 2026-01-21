package com.plaiaundi.sepe.seid.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

public record OpenDataIncidence(
        @Id
        @NotBlank
        int incidenceId,
        @NotBlank
        int sourceId,
        String autonomousRegion,
        String province,
        String carRegistration,
        String cause,
        String cityTown,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String road,
        double pkStart,
        double pkEnd,
        String direction,
        double latitude,
        double longitude,
        String incidenceDescription,
        String incidenceLevel,
        String incidenceType
) {}
