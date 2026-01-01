package com.example.demo.adapters.in.api.dto;

import jakarta.validation.constraints.NotNull;

public record RemoveTeamFromTournamentRequest(
        @NotNull Long organizerId,
        @NotNull Long teamId,
        String comment) {
}
