package com.example.demo.adapters.out.persistence.jpa.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.adapters.out.persistence.jpa.entities.TournamentParticipantEntity;

@Repository
public interface TournamentParticipantRepositoryJpa extends JpaRepository<TournamentParticipantEntity, Long> {

    boolean existsByTournamentIdAndUserId(Long tournamentId, Long userId);

    @Query("""
            SELECT tp.userId, COUNT(tp)
            FROM TournamentParticipantEntity tp
            WHERE tp.userId IN :userIds
            GROUP BY tp.userId
            """)
    List<Object[]> countByUserIds(@Param("userIds") List<Long> userIds);
}

