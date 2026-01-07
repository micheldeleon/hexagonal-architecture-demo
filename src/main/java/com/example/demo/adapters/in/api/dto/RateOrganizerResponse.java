package com.example.demo.adapters.in.api.dto;

public record RateOrganizerResponse(
    Long reputationId,
    Long organizerId,
    int scoreGiven,
    double newAverageScore,
    int totalRatings,
    String message
) {}
