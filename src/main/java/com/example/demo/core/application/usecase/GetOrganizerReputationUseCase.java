package com.example.demo.core.application.usecase;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.core.domain.models.Reputation;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.GetOrganizerReputationPort;
import com.example.demo.core.ports.out.ReputationRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

public class GetOrganizerReputationUseCase implements GetOrganizerReputationPort {

    private final ReputationRepositoryPort reputationRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public GetOrganizerReputationUseCase(
            ReputationRepositoryPort reputationRepositoryPort,
            UserRepositoryPort userRepositoryPort) {
        this.reputationRepositoryPort = reputationRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public OrganizerReputationResult getReputation(Long organizerId) {
        if (organizerId == null) {
            throw new IllegalArgumentException("organizerId es requerido");
        }

        // Verificar que el organizador existe
        User organizer = userRepositoryPort.findById(organizerId);
        if (organizer == null) {
            throw new IllegalArgumentException("Organizador no encontrado");
        }

        // Obtener estadísticas
        double average = reputationRepositoryPort.getAverageScore(organizerId);
        int total = reputationRepositoryPort.countRatingsByOrganizer(organizerId);
        
        // Obtener distribución
        var dist = reputationRepositoryPort.getDistribution(organizerId);
        RatingDistribution distribution = new RatingDistribution(
            dist.fiveStars(),
            dist.fourStars(),
            dist.threeStars(),
            dist.twoStars(),
            dist.oneStar()
        );

        // Obtener últimas 10 calificaciones
        List<Reputation> recentReputations = reputationRepositoryPort.findRecentByOrganizerId(organizerId, 10);
        List<RecentRating> recentRatings = recentReputations.stream()
            .map(rep -> new RecentRating(
                rep.getUser().getId(),
                rep.getUser().getName() + " " + rep.getUser().getLastName(),
                rep.getTournament() != null ? rep.getTournament().getId() : null,
                rep.getTournament() != null ? rep.getTournament().getName() : "N/A",
                rep.getScore(),
                rep.getComment(),
                rep.getCreatedAt() != null ? dateFormat.format(rep.getCreatedAt()) : ""
            ))
            .collect(Collectors.toList());

        return new OrganizerReputationResult(
            organizerId,
            organizer.getName() + " " + organizer.getLastName(),
            average,
            total,
            distribution,
            recentRatings
        );
    }
}
