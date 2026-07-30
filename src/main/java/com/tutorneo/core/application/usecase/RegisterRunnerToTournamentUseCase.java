package com.tutorneo.core.application.usecase;

import java.util.Date;
import java.util.List;

import com.tutorneo.adapters.in.api.dto.ParticipantRequest;
import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentModerationStatus;
import com.tutorneo.core.domain.models.TournamentStatus;
import com.tutorneo.core.domain.models.NotificationType;
import com.tutorneo.core.domain.models.Formats.RaceFormat;
import com.tutorneo.core.domain.models.User;
import com.tutorneo.core.ports.in.RegisterRunnerToTournamentPort;
import com.tutorneo.core.ports.in.CreateNotificationPort;
import com.tutorneo.core.ports.out.TeamRegistrationPort;
import com.tutorneo.core.ports.out.TournamentRepositoryPort;
import com.tutorneo.core.ports.out.UserRepositoryPort;
import com.tutorneo.core.ports.out.NotificationPort;
import com.tutorneo.adapters.in.api.dto.RunnerRegistrationRequest;

public class RegisterRunnerToTournamentUseCase implements RegisterRunnerToTournamentPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final TeamRegistrationPort teamRegistrationPort;
    private final UserRepositoryPort userRepositoryPort;
    private final NotificationPort notificationPort;
    private final CreateNotificationPort createNotificationPort;

    public RegisterRunnerToTournamentUseCase(TournamentRepositoryPort tournamentRepositoryPort,
            TeamRegistrationPort teamRegistrationPort,
            UserRepositoryPort userRepositoryPort,
            NotificationPort notificationPort,
            CreateNotificationPort createNotificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.teamRegistrationPort = teamRegistrationPort;
        this.userRepositoryPort = userRepositoryPort;
        this.notificationPort = notificationPort;
        this.createNotificationPort = createNotificationPort;
    }

    @Override
    public void register(Long tournamentId, String userEmail, RunnerRegistrationRequest request) {
        if (tournamentId == null || userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("tournamentId y userEmail son requeridos");
        }

        Tournament tournament = tournamentRepositoryPort.findById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        if (tournament.getModerationStatus() == TournamentModerationStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        if (!(tournament.getFormat() instanceof RaceFormat)) {
            throw new IllegalStateException("El formato del torneo no es carrera");
        }
        if (tournament.getStatus() != TournamentStatus.ABIERTO) {
            throw new IllegalStateException("El torneo no esta abierto para inscripciones");
        }
        Date now = new Date();
        if (tournament.getRegistrationDeadline() != null && tournament.getRegistrationDeadline().before(now)) {
            throw new IllegalStateException("El torneo ya cerro inscripciones");
        }

        User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String fullName = resolveFullName(user, request);
        String nationalId = resolveNationalId(user, request);

        String teamName = fullName;
        ParticipantRequest runner = new ParticipantRequest(fullName, nationalId);
        Long disciplineId = tournament.getDiscipline() != null ? tournament.getDiscipline().getId() : null;

        teamRegistrationPort.registerTeam(tournamentId, user.getId(), teamName, disciplineId, List.of(runner));

        // 👉 SUMAR +1 a teamsInscribed
        int current = tournament.getTeamsInscribed();
        tournament.setTeamsInscribed(current + 1);

        // 👉 Guardar cambios del torneo
        tournamentRepositoryPort.save(tournament, tournament.getOrganizer().getId());

        // 🔔 Notificar inscripción confirmada
        try {
            createNotificationPort.createNotification(
                user.getId(),
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

    private String resolveFullName(User user, RunnerRegistrationRequest request) {
        if (request != null && request.fullName() != null && !request.fullName().isBlank()) {
            return request.fullName().trim();
        }
        String fullName = (user.getName() != null ? user.getName() : "").trim();
        if (user.getLastName() != null && !user.getLastName().isBlank()) {
            fullName = (fullName + " " + user.getLastName()).trim();
        }
        if (fullName.isBlank()) {
            throw new IllegalArgumentException("El usuario debe tener nombre completo cargado");
        }
        return fullName;
    }

    private String resolveNationalId(User user, RunnerRegistrationRequest request) {
        if (user.getNationalId() != null && !user.getNationalId().isBlank()) {
            return user.getNationalId();
        }
        if (request != null && request.nationalId() != null && !request.nationalId().isBlank()) {
            return request.nationalId();
        }
        throw new IllegalArgumentException("Debe proporcionar cedula/nationalId");
    }
}
