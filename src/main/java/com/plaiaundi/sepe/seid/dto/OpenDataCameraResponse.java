package com.plaiaundi.sepe.seid.dto;

import java.util.List;

public record OpenDataCameraResponse(
    int totalItems,
    int totalPages,
    int currentPage,
    List<OpenDataCamera> cameras
) {}
