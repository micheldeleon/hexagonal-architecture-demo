package com.example.demo.adapters.in.api.dto;

public record RatingDistributionDto(
    int fiveStars,
    int fourStars,
    int threeStars,
    int twoStars,
    int oneStar
) {}
