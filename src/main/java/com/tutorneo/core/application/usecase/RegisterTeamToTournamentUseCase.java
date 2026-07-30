package com.tutorneo.core.application.usecase;

import java.util.Date;
import java.util.List;

import com.tutorneo.adapters.in.api.dto.ParticipantRequest;
import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentModerationStatus;
import com.tutorneo.core.domain.models.TournamentStatus;
import com.tutorneo.core.domain.models.NotificationType;
import com.tutorneo.core.domain.models.Formats.RaceFormat;
import com.tutorneo.core.ports.in.RegisterTeamToTournamentPort;
import com.tutorneo.core.ports.in.CreateNotificationPort;
import com.tutorneo.core.ports.out.TeamRegistrationPort;
import com.tutorneo.core.ports.out.TournamentRepositoryPort;
import com.tutorneo.core.ports.out.NotificationPort;

public class RegisterTeamToTournamentUseCase implements RegisterTeamToTournamentPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final TeamRegistrationPort teamRegistrationPort;
    private final NotificationPort notificationPort;
    private final CreateNotificationPort createNotificationPort;

    public RegisterTeamToTournamentUseCase(TournamentRepositoryPort tournamentRepositoryPort,
            TeamRegistrationPort teamRegistrationPort,
            NotificationPort notificationPort,
            CreateNotificationPort createNotificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.teamRegistrationPort = teamRegistrationPort;
        this.notificationPort = notificationPort;
        this.createNotificationPort = createNotificationPort;
    }

    @Override
    public void registerTeam(Long tournamentId, Long userId, String teamName, List<ParticipantRequest> participants) {
        if (tournamentId == null || userId == null) {
            throw new IllegalArgumentException("tournamentId y userId son requeridos");
        }
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("Se requiere al menos un participante");
        }

        Tournament tournament = tournamentRepositoryPort.findById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        if (tournament.getModerationStatus() == TournamentModerationStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }

        if (tournament.getFormat() instanceof RaceFormat && participants.size() != 1) {
            throw new IllegalStateException("Para formato carrera solo se permite un participante por equipo");
        }

        if (tournament.getStatus() != TournamentStatus.ABIERTO) {
            throw new IllegalStateException("El torneo no esta abierto para inscripciones");
        }

        Date now = new Date();
        if (tournament.getRegistrationDeadline() != null && tournament.getRegistrationDeadline().before(now)) {
            throw new IllegalStateException("El torneo ya cerro inscripciones");
        }

        Long disciplineId = tournament.getDiscipline() != null ? tournament.getDiscipline().getId() : null;

        // 👉 Registrar el equipo
        teamRegistrationPort.registerTeam(tournamentId, userId, teamName, disciplineId, participants);

        // 👉 SUMAR +1 a teamsInscribed
        int current = tournament.getTeamsInscribed();
        tournament.setTeamsInscribed(current + 1);

        // 👉 Guardar cambios del torneo
        tournamentRepositoryPort.save(tournament, tournament.getOrganizer().getId());

        // 🔔 Notificar inscripción confirmada al capitán
        try {
            createNotificationPort.createNotification(
                userId,
                NotificationType.REGISTRATION_CONFIRMED,
                "Equipo Inscrito",
                "El equipo '" + teamName + "' ha sido inscrito en el torneo '" + tournament.getName() + "'.",
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
