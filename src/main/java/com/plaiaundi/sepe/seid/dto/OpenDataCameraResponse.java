package com.plaiaundi.sepe.seid.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record OpenDataCameraResponse(
    @NotBlank
    int totalItems,
    @NotBlank
    int totalPages,
    @NotBlank
    int currentPage,
    List<OpenDataCamera> cameras
) {}
