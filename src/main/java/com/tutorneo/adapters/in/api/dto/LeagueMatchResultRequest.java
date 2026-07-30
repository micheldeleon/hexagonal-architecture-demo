package com.tutorneo.adapters.in.api.dto;

import jakarta.validation.constraints.NotNull;

public record LeagueMatchResultRequest(
        @NotNull Integer scoreHome,
        @NotNull Integer scoreAway) {
}
