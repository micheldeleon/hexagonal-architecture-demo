package com.tutorneo.core.application.usecase;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.tutorneo.core.domain.models.NotificationType;
import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentModerationStatus;
import com.tutorneo.core.domain.models.TournamentStatus;
import com.tutorneo.core.domain.models.User;
import com.tutorneo.core.ports.in.CreateNotificationPort;
import com.tutorneo.core.ports.in.LeaveTournamentPort;
import com.tutorneo.core.ports.out.TeamCaptainQueryPort;
import com.tutorneo.core.ports.out.TeamRemovalPort;
import com.tutorneo.core.ports.out.TournamentRegistrationPort;
import com.tutorneo.core.ports.out.TournamentRepositoryPort;
import com.tutorneo.core.ports.out.UserRepositoryPort;

public class LeaveTournamentUseCase implements LeaveTournamentPort {

    private static final Duration LEAVE_WINDOW = Duration.ofHours(24);

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final TournamentRegistrationPort tournamentRegistrationPort;
    private final TeamCaptainQueryPort teamCaptainQueryPort;
    private final TeamRemovalPort teamRemovalPort;
    private final CreateNotificationPort createNotificationPort;

    public LeaveTournamentUseCase(
            TournamentRepositoryPort tournamentRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            TournamentRegistrationPort tournamentRegistrationPort,
            TeamCaptainQueryPort teamCaptainQueryPort,
            TeamRemovalPort teamRemovalPort,
            CreateNotificationPort createNotificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.tournamentRegistrationPort = tournamentRegistrationPort;
        this.teamCaptainQueryPort = teamCaptainQueryPort;
        this.teamRemovalPort = teamRemovalPort;
        this.createNotificationPort = createNotificationPort;
    }

    @Override
    @Transactional
    public LeaveTournamentResult leave(Long tournamentId, String userEmail, Long teamId, String reason) {
        if (tournamentId == null) {
            throw new IllegalArgumentException("tournamentId es requerido");
        }
        if (userEmail == null || userEmail.isBlank()) {
            throw new SecurityException("No se pudo determinar el usuario autenticado");
        }

        User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Tournament tournament = tournamentRepositoryPort.findById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        if (tournament.getModerationStatus() == TournamentModerationStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }

        if (tournament.getOrganizer() != null && tournament.getOrganizer().getId() != null
                && tournament.getOrganizer().getId().equals(user.getId())) {
            throw new SecurityException("El organizador no puede salir del torneo");
        }

        if (tournament.getStatus() != TournamentStatus.ABIERTO) {
            throw new IllegalStateException("Solo se puede salir de torneos en estado ABIERTO");
        }

        Date nowDate = new Date();

        // Si hay deadline de inscripción, no permitimos salir después de ese momento.
        if (tournament.getRegistrationDeadline() != null && tournament.getRegistrationDeadline().before(nowDate)) {
            throw new IllegalStateException("No se puede salir luego de la fecha límite de inscripción");
        }

        if (tournament.getStartAt() == null) {
            throw new IllegalStateException("El torneo no tiene fecha de inicio configurada");
        }

        Instant now = nowDate.toInstant();
        Instant start = tournament.getStartAt().toInstant();
        Duration untilStart = Duration.between(now, start);
        if (untilStart.compareTo(LEAVE_WINDOW) < 0) {
            throw new IllegalStateException("No se puede salir a menos de 24 horas del inicio del torneo");
        }

        // 1) Si está inscripto individualmente, sale individual.
        if (tournamentRegistrationPort.exists(tournamentId, user.getId())) {
            long removed = tournamentRegistrationPort.unregister(tournamentId, user.getId());
            if (removed <= 0) {
                throw new IllegalStateException("No estás inscripto en el torneo");
            }

            tournament.setTeamsInscribed(Math.max(0, tournament.getTeamsInscribed() - 1));
            tournamentRepositoryPort.save(tournament, tournament.getOrganizer().getId());

            notifyLeave(tournament, user, LeaveMode.INDIVIDUAL, null, reason);
            return new LeaveTournamentResult(tournamentId, LeaveMode.INDIVIDUAL, null);
        }

        // 2) Si es capitán de un equipo inscripto, al salir se retira el equipo completo.
        List<Long> captainTeamIds = teamCaptainQueryPort.findCaptainTeamIdsInTournament(tournamentId, user.getId());
        if (captainTeamIds.isEmpty()) {
            throw new IllegalStateException("No estás inscripto en el torneo");
        }

        Long selectedTeamId = resolveTeamId(captainTeamIds, teamId);
        teamRemovalPort.removeTeamFromTournament(tournamentId, selectedTeamId);

        tournament.setTeamsInscribed(Math.max(0, tournament.getTeamsInscribed() - 1));
        tournamentRepositoryPort.save(tournament, tournament.getOrganizer().getId());

        notifyLeave(tournament, user, LeaveMode.TEAM, selectedTeamId, reason);
        return new LeaveTournamentResult(tournamentId, LeaveMode.TEAM, selectedTeamId);
    }

    private Long resolveTeamId(List<Long> captainTeamIds, Long requestedTeamId) {
        if (captainTeamIds.size() == 1) {
            return captainTeamIds.getFirst();
        }
        if (requestedTeamId == null) {
            throw new IllegalArgumentException("Debes indicar teamId");
        }
        if (!captainTeamIds.contains(requestedTeamId)) {
            throw new IllegalArgumentException("teamId inválido para este torneo");
        }
        return requestedTeamId;
    }

    private void notifyLeave(Tournament tournament, User user, LeaveMode mode, Long teamId, String reason) {
        Long organizerId = tournament.getOrganizer() != null ? tournament.getOrganizer().getId() : null;
        String suffix = (reason != null && !reason.isBlank()) ? (" Motivo: " + reason.trim()) : "";

        try {
            if (mode == LeaveMode.INDIVIDUAL) {
                createNotificationPort.createNotification(
                        user.getId(),
                        NotificationType.TOURNAMENT_LEFT,
                        "Salida del torneo",
                        "Saliste del torneo '" + tournament.getName() + "'." + suffix,
                        tournament.getId());
                if (organizerId != null) {
                    createNotificationPort.createNotification(
                            organizerId,
                            NotificationType.TOURNAMENT_LEFT,
                            "Participante se retiró",
                            "Un participante se retiró del torneo '" + tournament.getName() + "'." + suffix,
                            tournament.getId());
                }
            } else {
                createNotificationPort.createNotification(
                        user.getId(),
                        NotificationType.TEAM_WITHDRAWN,
                        "Equipo retirado",
                        "Tu equipo fue retirado del torneo '" + tournament.getName() + "'." + suffix,
                        tournament.getId());
                if (organizerId != null) {
                    createNotificationPort.createNotification(
                            organizerId,
                            NotificationType.TEAM_WITHDRAWN,
                            "Equipo se retiró",
                            "Un equipo se retiró del torneo '" + tournament.getName() + "'." + suffix,
                            tournament.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("Error enviando notificación de salida de torneo: " + e.getMessage());
        }
    }
}

