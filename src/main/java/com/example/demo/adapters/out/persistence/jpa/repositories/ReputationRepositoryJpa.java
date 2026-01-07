package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.adapters.out.persistence.jpa.entities.ReputationEntity;

public interface ReputationRepositoryJpa extends JpaRepository<ReputationEntity, Long> {
    
    /**
     * Verifica si existe una calificación de un usuario a un organizador en un torneo
     */
    boolean existsByUserIdAndOrganizerIdAndTournamentId(Long userId, Long organizerId, Long tournamentId);
    
    /**
     * Obtiene todas las reputaciones de un organizador
     */
    List<ReputationEntity> findByOrganizerIdOrderByCreatedAtDesc(Long organizerId);
    
    /**
     * Obtiene el promedio de score de un organizador
     */
    @Query("SELECT AVG(r.score) FROM ReputationEntity r WHERE r.organizerId = :organizerId")
    Double getAverageScoreByOrganizerId(@Param("organizerId") Long organizerId);
    
    /**
     * Cuenta total de calificaciones de un organizador
     */
    long countByOrganizerId(Long organizerId);
    
    /**
     * Cuenta cuántas calificaciones de cada puntaje tiene un organizador
     */
    @Query("SELECT r.score, COUNT(r) FROM ReputationEntity r WHERE r.organizerId = :organizerId GROUP BY r.score")
    List<Object[]> getDistributionByOrganizerId(@Param("organizerId") Long organizerId);
}
