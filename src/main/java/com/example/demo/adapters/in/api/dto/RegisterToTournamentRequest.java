package com.example.demo.adapters.in.api.dto;

import jakarta.validation.constraints.NotNull;

public record RegisterToTournamentRequest(
    @NotNull Long userId
) {}

