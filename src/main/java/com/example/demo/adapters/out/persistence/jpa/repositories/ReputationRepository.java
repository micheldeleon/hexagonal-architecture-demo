package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.example.demo.adapters.out.persistence.jpa.entities.ReputationEntity;
import com.example.demo.core.domain.models.Organizer;
import com.example.demo.core.domain.models.Reputation;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.out.ReputationRepositoryPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component
public class ReputationRepository implements ReputationRepositoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    private final ReputationRepositoryJpa reputationRepositoryJpa;
    private final UserRepositoryPort userRepositoryPort;
    private final TournamentRepositoryPort tournamentRepositoryPort;

    public ReputationRepository(
            ReputationRepositoryJpa reputationRepositoryJpa,
            @Lazy UserRepositoryPort userRepositoryPort,
            @Lazy TournamentRepositoryPort tournamentRepositoryPort) {
        this.reputationRepositoryJpa = reputationRepositoryJpa;
        this.userRepositoryPort = userRepositoryPort;
        this.tournamentRepositoryPort = tournamentRepositoryPort;
    }

    @Override
    @Transactional
    public Reputation save(Reputation reputation) {
        ReputationEntity entity = toEntity(reputation);
        ReputationEntity saved = reputationRepositoryJpa.save(entity);
        return toModel(saved);
    }

    @Override
    public boolean hasUserRatedOrganizerInTournament(Long userId, Long organizerId, Long tournamentId) {
        return reputationRepositoryJpa.existsByUserIdAndOrganizerIdAndTournamentId(userId, organizerId, tournamentId);
    }

    @Override
    public double getAverageScore(Long organizerId) {
        Double average = reputationRepositoryJpa.getAverageScoreByOrganizerId(organizerId);
        return average != null ? average : 0.0;
    }

    @Override
    public int countRatingsByOrganizer(Long organizerId) {
        return (int) reputationRepositoryJpa.countByOrganizerId(organizerId);
    }

    @Override
    public List<Reputation> findByOrganizerId(Long organizerId) {
        List<ReputationEntity> entities = reputationRepositoryJpa.findByOrganizerIdOrderByCreatedAtDesc(organizerId);
        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reputation> findRecentByOrganizerId(Long organizerId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<ReputationEntity> entities = entityManager
                .createQuery("SELECT r FROM ReputationEntity r WHERE r.organizerId = :organizerId ORDER BY r.createdAt DESC", ReputationEntity.class)
                .setParameter("organizerId", organizerId)
                .setMaxResults(limit)
                .getResultList();
        
        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public RatingDistribution getDistribution(Long organizerId) {
        List<Object[]> results = reputationRepositoryJpa.getDistributionByOrganizerId(organizerId);
        
        Map<Integer, Long> distribution = new HashMap<>();
        distribution.put(1, 0L);
        distribution.put(2, 0L);
        distribution.put(3, 0L);
        distribution.put(4, 0L);
        distribution.put(5, 0L);
        
        for (Object[] result : results) {
            Integer score = (Integer) result[0];
            Long count = (Long) result[1];
            distribution.put(score, count);
        }
        
        return new RatingDistribution(
            distribution.get(5).intValue(),
            distribution.get(4).intValue(),
            distribution.get(3).intValue(),
            distribution.get(2).intValue(),
            distribution.get(1).intValue()
        );
    }

    private ReputationEntity toEntity(Reputation model) {
        ReputationEntity entity = new ReputationEntity();
        entity.setId(model.getId());
        entity.setUserId(model.getUser().getId());
        entity.setOrganizerId(model.getOrganizer().getId());
        entity.setTournamentId(model.getTournament().getId());
        entity.setScore(model.getScore());
        entity.setComment(model.getComment());
        entity.setCreatedAt(model.getCreatedAt());
        return entity;
    }

    private Reputation toModel(ReputationEntity entity) {
        User user = userRepositoryPort.findById(entity.getUserId());
        User organizerUser = userRepositoryPort.findById(entity.getOrganizerId());
        Tournament tournament = tournamentRepositoryPort.findById(entity.getTournamentId());
        
        Organizer organizer = new Organizer();
        if (organizerUser != null) {
            organizer.setId(organizerUser.getId());
            organizer.setName(organizerUser.getName());
            organizer.setLastName(organizerUser.getLastName());
            organizer.setEmail(organizerUser.getEmail());
        }
        
        return new Reputation(
            entity.getId(),
            user,
            organizer,
            tournament,
            entity.getScore(),
            entity.getComment(),
            entity.getCreatedAt()
        );
    }
}
