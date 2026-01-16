package com.plaiaundi.sepe.seid.dto;

import java.util.List;

public record OpenDataIncidenceResponse(
   int totalItems,
   int totalPages,
   int currentPage,
   List<OpenDataIncidence> incidences
) {}
