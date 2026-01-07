package com.example.demo.adapters.in.api.dto;

import java.util.List;

public record OrganizerReputationResponse(
    Long organizerId,
    String organizerName,
    double averageScore,
    int totalRatings,
    RatingDistributionDto distribution,
    List<RecentRatingDto> recentRatings
) {}
