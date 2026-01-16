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
        String startDate,
        String road,
        String pkStart,
        String pkEnd,
        String direction,
        String latitude,
        String longitude
) {}
