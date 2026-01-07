package com.example.demo.core.ports.in;

import java.util.List;

public interface GetOrganizerReputationPort {
    
    /**
     * Obtiene la reputación completa de un organizador
     */
    OrganizerReputationResult getReputation(Long organizerId);
    
    /**
     * Resultado con toda la información de reputación
     */
    record OrganizerReputationResult(
        Long organizerId,
        String organizerName,
        double averageScore,
        int totalRatings,
        RatingDistribution distribution,
        List<RecentRating> recentRatings
    ) {}
    
    record RatingDistribution(
        int fiveStars,
        int fourStars,
        int threeStars,
        int twoStars,
        int oneStar
    ) {}
    
    record RecentRating(
        Long userId,
        String userName,
        Long tournamentId,
        String tournamentName,
        int score,
        String comment,
        String createdAt
    ) {}
}
