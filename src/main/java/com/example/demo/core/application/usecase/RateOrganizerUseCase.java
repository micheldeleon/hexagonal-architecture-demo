package com.example.demo.core.application.usecase;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.domain.models.Organizer;
import com.example.demo.core.domain.models.Reputation;
import com.example.demo.core.ports.in.RateOrganizerPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.core.ports.out.ReputationRepositoryPort;

public class RateOrganizerUseCase implements RateOrganizerPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final ReputationRepositoryPort reputationRepositoryPort;

    public RateOrganizerUseCase(
            TournamentRepositoryPort tournamentRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            ReputationRepositoryPort reputationRepositoryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.reputationRepositoryPort = reputationRepositoryPort;
    }

    @Override
    public RateOrganizerResult rate(Long userId, Long organizerId, Long tournamentId, int score, String comment) {
        // 1. Validar parámetros básicos
        if (userId == null || organizerId == null || tournamentId == null) {
            throw new IllegalArgumentException("userId, organizerId y tournamentId son requeridos");
        }

        if (userId.equals(organizerId)) {
            throw new IllegalStateException("No puedes calificarte a ti mismo");
        }

        // 2. Validar score (1-5)
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5 estrellas");
        }

        // 3. Verificar que el usuario existe
        User user = userRepositoryPort.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // 4. Verificar que el organizador existe
        User organizerUser = userRepositoryPort.findById(organizerId);
        if (organizerUser == null) {
            throw new IllegalArgumentException("Organizador no encontrado");
        }

        // El organizador debe ser una instancia de Organizer (o tener el rol)
        // Por ahora asumimos que si tiene rol de organizador, es válido
        Organizer organizer = new Organizer();
        organizer.setId(organizerId);
        organizer.setName(organizerUser.getName());
        organizer.setLastName(organizerUser.getLastName());
        organizer.setEmail(organizerUser.getEmail());

        // 5. Verificar que el torneo existe (cargar con equipos para verificar participación)
        Tournament tournament = tournamentRepositoryPort.findByIdWithTeams(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }

        // 6. Verificar que el torneo está FINALIZADO
        if (tournament.getStatus() != TournamentStatus.FINALIZADO) {
            throw new IllegalStateException("Solo puedes calificar torneos finalizados");
        }

        // 7. Verificar que el organizador del torneo es quien estamos calificando
        if (tournament.getOrganizer() == null || !tournament.getOrganizer().getId().equals(organizerId)) {
            throw new IllegalStateException("Este usuario no es el organizador de este torneo");
        }

        // 8. Verificar que el usuario PARTICIPÓ en el torneo (comparar por nationalId)
        String userNationalId = user.getNationalId();
        if (userNationalId == null || userNationalId.isBlank()) {
            throw new IllegalStateException("Tu usuario no tiene una cédula registrada");
        }
        boolean participated = tournament.isParticipantByNationalId(userNationalId);
        if (!participated) {
            throw new IllegalStateException("Solo los participantes del torneo pueden calificar al organizador");
        }

        // 9. Verificar que NO haya calificado previamente
        boolean alreadyRated = reputationRepositoryPort.hasUserRatedOrganizerInTournament(userId, organizerId, tournamentId);
        if (alreadyRated) {
            throw new IllegalStateException("Ya has calificado a este organizador en este torneo");
        }

        // 10. Crear la reputación
        Reputation reputation = new Reputation(user, organizer, tournament, score, comment);
        Reputation savedReputation = reputationRepositoryPort.save(reputation);

        // 11. Obtener estadísticas actualizadas
        double newAverage = reputationRepositoryPort.getAverageScore(organizerId);
        int totalRatings = reputationRepositoryPort.countRatingsByOrganizer(organizerId);

        // 12. Retornar resultado
        return new RateOrganizerResult(
            savedReputation.getId(),
            organizerId,
            score,
            newAverage,
            totalRatings
        );
    }
}
