package com.tutorneo.adapters.in.api.dto;

public record RecentRatingDto(
    Long userId,
    String userName,
    Long tournamentId,
    String tournamentName,
    int score,
    String comment,
    String createdAt
) {}
