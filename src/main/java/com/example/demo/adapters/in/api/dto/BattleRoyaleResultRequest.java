package com.example.demo.adapters.in.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BattleRoyaleResultRequest(
        @NotNull Long teamId,
        @NotNull @Min(1) Integer position) {
}
