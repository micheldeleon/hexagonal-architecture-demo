package com.tutorneo.adapters.in.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RaceResultRequest(
        @NotNull Long teamId,
        @NotNull @Min(1) Long timeMillis) {
}
