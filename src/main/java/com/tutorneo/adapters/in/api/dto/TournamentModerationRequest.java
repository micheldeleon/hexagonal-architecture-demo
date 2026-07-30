package com.tutorneo.adapters.in.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TournamentModerationRequest(
        @NotBlank String reason) {
}

