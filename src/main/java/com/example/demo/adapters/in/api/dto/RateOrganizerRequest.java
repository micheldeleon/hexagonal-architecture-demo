package com.example.demo.adapters.in.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RateOrganizerRequest(
    @NotNull(message = "El ID del torneo es requerido")
    Long tournamentId,
    
    @NotNull(message = "La calificación es requerida")
    @Min(value = 1, message = "La calificación mínima es 1 estrella")
    @Max(value = 5, message = "La calificación máxima es 5 estrellas")
    Integer score,
    
    String comment  // Opcional
) {}
