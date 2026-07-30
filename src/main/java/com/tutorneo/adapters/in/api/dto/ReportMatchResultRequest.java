package com.tutorneo.adapters.in.api.dto;

import jakarta.validation.constraints.NotNull;

public record ReportMatchResultRequest(
        Integer scoreHome,
        Integer scoreAway,
        @NotNull Long winnerTeamId) {
}
