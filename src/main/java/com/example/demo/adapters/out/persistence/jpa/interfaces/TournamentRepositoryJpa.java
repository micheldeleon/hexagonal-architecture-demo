package com.example.demo.adapters.out.persistence.jpa.interfaces;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.adapters.out.persistence.jpa.entities.TournamentJpaEntity;

public interface TournamentRepositoryJpa extends JpaRepository<TournamentJpaEntity, Long> {

        @Query("""
                            SELECT t FROM TournamentJpaEntity t
                            WHERE (:status IS NULL OR t.status = :status)
                              AND (:disciplineId IS NULL OR t.discipline.id = :disciplineId)
                              AND (:namePattern = '' OR LOWER(t.name) LIKE :namePattern)
                              AND (:startFrom IS NULL OR t.startAt >= :startFrom)
                              AND (:startTo IS NULL OR t.startAt <= :startTo)
                              AND (
                                    :withPrize IS NULL
                                    OR (:withPrize = TRUE AND t.prize IS NOT NULL AND t.prize <> '')
                                    OR (:withPrize = FALSE AND (t.prize IS NULL OR t.prize = ''))
                                  )
                              AND (
                                    :withCost IS NULL
                                    OR (:withCost = TRUE AND t.registrationCost > 0)
                                    OR (:withCost = FALSE AND t.registrationCost = 0)
                                  )
                        """)
        List<TournamentJpaEntity> findByFilters(
                        @Param("status") String status,
                        @Param("disciplineId") Long disciplineId,
                        @Param("namePattern") String namePattern,
                        @Param("startFrom") OffsetDateTime startFrom,
                        @Param("startTo") OffsetDateTime startTo,
                        @Param("withPrize") Boolean withPrize,
                        @Param("withCost") Boolean withCost);

        List<TournamentJpaEntity> findByStatus(String status);
        
        @Query("SELECT t FROM TournamentJpaEntity t ORDER BY t.createdAt DESC LIMIT 3")
        List<TournamentJpaEntity> findTop3ByOrderByCreatedAtDesc();

        @Query("""
                        SELECT t.organizerId, COUNT(t)
                        FROM TournamentJpaEntity t
                        WHERE t.organizerId IN :userIds
                        GROUP BY t.organizerId
                        """)
        List<Object[]> countByOrganizerIds(@Param("userIds") List<Long> userIds);
}
