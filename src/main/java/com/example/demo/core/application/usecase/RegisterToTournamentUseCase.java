package com.example.demo.core.application.usecase;

import java.util.Date;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.ports.in.RegisterToTournamentPort;
import com.example.demo.core.ports.in.CreateNotificationPort;
import com.example.demo.core.ports.out.TournamentRegistrationPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.NotificationPort;

public class RegisterToTournamentUseCase implements RegisterToTournamentPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final TournamentRegistrationPort tournamentRegistrationPort;
    private final NotificationPort notificationPort;
    private final CreateNotificationPort createNotificationPort;

    public RegisterToTournamentUseCase(TournamentRepositoryPort tournamentRepositoryPort,
            TournamentRegistrationPort tournamentRegistrationPort,
            NotificationPort notificationPort,
            CreateNotificationPort createNotificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.tournamentRegistrationPort = tournamentRegistrationPort;
        this.notificationPort = notificationPort;
        this.createNotificationPort = createNotificationPort;
    }

    @Override
    public void register(Long tournamentId, Long userId) {
        if (tournamentId == null || userId == null) {
            throw new IllegalArgumentException("tournamentId y userId son requeridos");
        }

        Tournament tournament = tournamentRepositoryPort.findById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        if (tournament.getModerationStatus() == TournamentModerationStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }

        if (tournament.getStatus() != TournamentStatus.ABIERTO) {
            throw new IllegalStateException("El torneo no está abierto para inscripciones");
        }

        Date now = new Date();
        if (tournament.getRegistrationDeadline() != null && tournament.getRegistrationDeadline().before(now)) {
            throw new IllegalStateException("El torneo ya cerró inscripciones");
        }

        if (tournamentRegistrationPort.exists(tournamentId, userId)) {
            throw new IllegalStateException("El usuario ya está inscrito en el torneo");
        }

        tournamentRegistrationPort.register(tournamentId, userId);

        // 👉 SUMAR +1 a teamsInscribed
        int current = tournament.getTeamsInscribed();
        tournament.setTeamsInscribed(current + 1);

        // 👉 Guardar cambios del torneo
        tournamentRepositoryPort.save(tournament, tournament.getOrganizer().getId());

        // 🔔 Notificar inscripción confirmada
        try {
            createNotificationPort.createNotification(
                userId,
                NotificationType.REGISTRATION_CONFIRMED,
                "Inscripción Confirmada",
                "Te has inscrito exitosamente al torneo '" + tournament.getName() + "'.",
                tournamentId
            );
        } catch (Exception e) {
            System.err.println("Error enviando notificación de inscripción: " + e.getMessage());
        }

        // 🔔 Notificar si el torneo está lleno
        if (tournament.getMaxParticipantsPerTournament() > 0 && tournament.getTeamsInscribed() >= tournament.getMaxParticipantsPerTournament()) {
            try {
                notificationPort.notifyUsersOfTournament(
                    tournamentId,
                    "Torneo Completo",
                    "El torneo '" + tournament.getName() + "' ha alcanzado el máximo de participantes.",
                    NotificationType.TOURNAMENT_FULL
                );
            } catch (Exception e) {
                System.err.println("Error enviando notificación de torneo lleno: " + e.getMessage());
            }
        }
    }
}
