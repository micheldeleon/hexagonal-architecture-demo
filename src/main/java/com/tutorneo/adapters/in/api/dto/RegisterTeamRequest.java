package com.tutorneo.adapters.in.api.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RegisterTeamRequest(
        @NotNull Long userId,
        @NotBlank String teamName,
        @NotEmpty List<@Valid ParticipantRequest> participants) {
}
