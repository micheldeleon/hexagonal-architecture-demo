package com.example.demo.core.ports.in;

public interface RateOrganizerPort {
    
    /**
     * Califica a un organizador por su desempeño en un torneo específico.
     * 
     * @param userId ID del usuario que califica (debe haber participado en el torneo)
     * @param organizerId ID del organizador a calificar
     * @param tournamentId ID del torneo donde participó
     * @param score Puntuación de 1 a 5 estrellas
     * @param comment Comentario opcional sobre la experiencia
     * @return La reputación creada
     */
    RateOrganizerResult rate(Long userId, Long organizerId, Long tournamentId, int score, String comment);
    
    /**
     * Resultado de la calificación
     */
    record RateOrganizerResult(
        Long reputationId,
        Long organizerId,
        int score,
        double newAverage,
        int totalRatings
    ) {}
}
