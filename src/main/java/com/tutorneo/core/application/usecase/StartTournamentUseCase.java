package com.tutorneo.core.application.usecase;

import java.util.Date;

import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentModerationStatus;
import com.tutorneo.core.domain.models.TournamentStatus;
import com.tutorneo.core.domain.models.NotificationType;
import com.tutorneo.core.domain.models.Format;
import com.tutorneo.core.domain.models.Formats.EliminationFormat;
import com.tutorneo.core.domain.models.Formats.LeagueFormat;
import com.tutorneo.core.domain.models.Formats.RaceFormat;
import com.tutorneo.core.domain.models.User;
import com.tutorneo.core.ports.in.StartTournamentPort;
import com.tutorneo.core.ports.out.TournamentRepositoryPort;
import com.tutorneo.core.ports.out.UserRepositoryPort;
import com.tutorneo.core.ports.out.NotificationPort;

public class StartTournamentUseCase implements StartTournamentPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final NotificationPort notificationPort;

    public StartTournamentUseCase(TournamentRepositoryPort tournamentRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            NotificationPort notificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public StartTournamentResult start(Long tournamentId, String organizerEmail) {
        // 1. Validar parámetros
        if (tournamentId == null) {
            throw new IllegalArgumentException("tournamentId es requerido");
        }
        if (organizerEmail == null || organizerEmail.isBlank()) {
            throw new SecurityException("No se pudo determinar el usuario autenticado");
        }

        // 2. Obtener el usuario organizador
        User organizer = userRepositoryPort.findByEmail(organizerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 3. Obtener el torneo
        Tournament tournament = tournamentRepositoryPort.findById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        if (tournament.getModerationStatus() == TournamentModerationStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }

        // 4. Verificar que el usuario es el organizador
        if (tournament.getOrganizer() == null || tournament.getOrganizer().getId() == null
                || !tournament.getOrganizer().getId().equals(organizer.getId())) {
            throw new SecurityException("Solo el organizador puede iniciar el torneo");
        }

        // 5. Verificar que el torneo está en estado ABIERTO
        if (tournament.getStatus() != TournamentStatus.ABIERTO) {
            throw new IllegalStateException("Solo se puede iniciar un torneo en estado ABIERTO");
        }

        // 6. Verificar cantidad mínima de participantes
        int currentParticipants = tournament.getTeamsInscribed();
        if (tournament.getMinParticipantsPerTournament() > 0 
                && currentParticipants < tournament.getMinParticipantsPerTournament()) {
            throw new IllegalStateException(
                    "El torneo necesita al menos " + tournament.getMinParticipantsPerTournament()
                            + " participantes. Actualmente hay " + currentParticipants);
        }

        // 7. Validar fechas - solo se puede iniciar antes de la fecha de inicio si está lleno
        Date now = new Date();
        Date startDate = tournament.getStartAt();
        
        boolean isFull = tournament.getMaxParticipantsPerTournament() > 0 
                && currentParticipants >= tournament.getMaxParticipantsPerTournament();
        
        // Si no está lleno y estamos antes de la fecha de inicio, no se puede iniciar
        if (!isFull && startDate != null && now.before(startDate)) {
            // Verificar si al menos llegó la fecha límite de inscripción
            Date registrationDeadline = tournament.getRegistrationDeadline();
            if (registrationDeadline == null || now.before(registrationDeadline)) {
                throw new IllegalStateException(
                        "El torneo solo puede iniciarse antes de su fecha de inicio si está lleno. "
                        + "Actualmente tiene " + currentParticipants + " de " 
                        + tournament.getMaxParticipantsPerTournament() + " participantes");
            }
        }

        // 8. Cambiar el estado a INICIADO
        tournament.setStatus(TournamentStatus.INICIADO);
        
        // 9. Guardar cambios
        Date startedAt = new Date();
        tournamentRepositoryPort.update(tournament);

        // 10. 🔔 Notificar a todos los participantes
        String notificationTitle = "Torneo Iniciado";
        String notificationMessage = buildNotificationMessage(tournament);
        
        try {
            notificationPort.notifyUsersOfTournament(
                    tournamentId,
                    notificationTitle,
                    notificationMessage,
                    NotificationType.TOURNAMENT_STARTED);
        } catch (Exception e) {
            System.err.println("Error enviando notificaciones de inicio de torneo: " + e.getMessage());
        }

        // 11. Retornar el resultado
        return new StartTournamentResult(
                tournamentId,
                organizer.getName(),
                startedAt,
                currentParticipants);
    }

    private String buildNotificationMessage(Tournament tournament) {
        Format format = tournament.getFormat();
        String tournamentName = tournament.getName();
        
        if (format instanceof EliminationFormat) {
            return "¡El torneo '" + tournamentName + "' ha comenzado! A la brevedad se dará a conocer el fixture de eliminación.";
        } else if (format instanceof LeagueFormat) {
            return "¡El torneo '" + tournamentName + "' ha comenzado! A la brevedad se dará a conocer el fixture de liga.";
        } else if (format instanceof RaceFormat) {
            return "¡El torneo '" + tournamentName + "' ha comenzado! Prepárate para la competencia.";
        } else {
            // Formato genérico o battle royale
            return "¡El torneo '" + tournamentName + "' ha comenzado! A la brevedad se darán a conocer los detalles.";
        }
    }
}
