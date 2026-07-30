package com.tutorneo.adapters.in.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ParticipantRequest(
        @NotBlank String fullName,
        @NotBlank String nationalId) {
}
