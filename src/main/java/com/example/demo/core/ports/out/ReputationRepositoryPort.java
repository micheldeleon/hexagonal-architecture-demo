package com.example.demo.core.ports.out;

import java.util.List;

import com.example.demo.core.domain.models.Reputation;

public interface ReputationRepositoryPort {
    
    /**
     * Guarda una nueva reputación
     */
    Reputation save(Reputation reputation);
    
    /**
     * Verifica si un usuario ya calificó a un organizador en un torneo específico
     */
    boolean hasUserRatedOrganizerInTournament(Long userId, Long organizerId, Long tournamentId);
    
    /**
     * Obtiene el promedio de calificaciones de un organizador
     */
    double getAverageScore(Long organizerId);
    
    /**
     * Cuenta el total de calificaciones de un organizador
     */
    int countRatingsByOrganizer(Long organizerId);
    
    /**
     * Obtiene todas las reputaciones de un organizador
     */
    List<Reputation> findByOrganizerId(Long organizerId);
    
    /**
     * Obtiene las últimas N reputaciones de un organizador
     */
    List<Reputation> findRecentByOrganizerId(Long organizerId, int limit);
    
    /**
     * Obtiene la distribución de calificaciones (cuántos 5★, 4★, etc.)
     */
    RatingDistribution getDistribution(Long organizerId);
    
    /**
     * Clase para representar la distribución de ratings
     */
    record RatingDistribution(
        int fiveStars,
        int fourStars,
        int threeStars,
        int twoStars,
        int oneStar
    ) {}
}
