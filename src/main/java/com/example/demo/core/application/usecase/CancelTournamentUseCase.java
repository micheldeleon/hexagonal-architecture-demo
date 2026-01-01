package com.example.demo.core.application.usecase;

import java.util.Date;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.ports.in.CancelTournamentPort;
import com.example.demo.core.ports.out.TournamentCleanupPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.core.ports.out.NotificationPort;

public class CancelTournamentUseCase implements CancelTournamentPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final TournamentCleanupPort tournamentCleanupPort;
    private final UserRepositoryPort userRepositoryPort;
    private final NotificationPort notificationPort;

    public CancelTournamentUseCase(
            TournamentRepositoryPort tournamentRepositoryPort,
            TournamentCleanupPort tournamentCleanupPort,
            UserRepositoryPort userRepositoryPort,
            NotificationPort notificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.tournamentCleanupPort = tournamentCleanupPort;
        this.userRepositoryPort = userRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public CancelTournamentResult cancel(Long tournamentId, String userEmail) {
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

        if (tournament.getOrganizer() == null || tournament.getOrganizer().getId() == null
                || !tournament.getOrganizer().getId().equals(user.getId())) {
            throw new SecurityException("Solo el organizador puede cancelar el torneo");
        }

        if (tournament.getStatus() != TournamentStatus.ABIERTO) {
            throw new IllegalStateException("Solo se puede cancelar un torneo en estado ABIERTO");
        }

        //Llamo al puerto de limpieza para que elimine las inscripciones y equipos asociados al torneo.
        tournamentCleanupPort.removeTeamsAndRegistrations(tournamentId);

        // actualizar estado
        tournament.setStatus(TournamentStatus.CANCELADO);
        tournament.setTeamsInscribed(0);

        Date canceledAt = new Date();
        tournamentRepositoryPort.update(tournament);

        // Enviar notificaciones a todos los participantes del torneo
        String notificationTitle = "Torneo Cancelado";
        String notificationMessage = "El torneo '" + tournament.getName() + "' ha sido cancelado por el organizador.";
        
        try {
            notificationPort.notifyUsersOfTournament(tournamentId, notificationTitle, notificationMessage, NotificationType.TOURNAMENT_CANCELED);
        } catch (Exception e) {
            // Log the error but don't fail the operation
            System.err.println("Error enviando notificaciones: " + e.getMessage());
        }

        //Retorna el metodo con los datos de la cancelación que habia definido en la interfaz para mostrar el resultado.
        return new CancelTournamentResult(tournamentId, user.getName(), canceledAt);
    }
}
