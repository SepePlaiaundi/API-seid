package com.plaiaundi.sepe.seid.dto;

import jakarta.persistence.Id;

public record OpenDataIncidence (
        @Id
        String incidenceId,
        String sourceId,
        String autonomousRegion,
        String province,
        String carRegistration,
        String cause,
        String cityTown,
        String startDate,
        String endDate,
        String road,
        String pkStart,
        String pkEnd,
        String direction,
        String latitude,
        String longitude,
        String incidenceDescription,
        String incidenceLevel,
        String incidenceType
) {}
