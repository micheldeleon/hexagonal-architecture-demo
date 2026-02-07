package com.example.demo.core.application.usecase;

import java.util.Date;

import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.AdminDeactivateTournamentPort;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

public class AdminDeactivateTournamentUseCase implements AdminDeactivateTournamentPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final NotificationPort notificationPort;

    public AdminDeactivateTournamentUseCase(
            TournamentRepositoryPort tournamentRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            NotificationPort notificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public DeactivateResult deactivate(Long tournamentId, String adminEmail, String reason) {
        if (tournamentId == null) {
            throw new IllegalArgumentException("tournamentId es requerido");
        }
        if (adminEmail == null || adminEmail.isBlank()) {
            throw new SecurityException("No se pudo determinar el usuario autenticado");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason es requerido");
        }

        User admin = userRepositoryPort.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("Admin no encontrado"));

        Tournament tournament = tournamentRepositoryPort.findById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }

        if (tournament.getModerationStatus() == TournamentModerationStatus.DEACTIVATED) {
            return new DeactivateResult(
                    tournamentId,
                    TournamentModerationStatus.DEACTIVATED,
                    tournament.getModeratedAt(),
                    tournament.getModeratedByAdminId(),
                    tournament.getModerationReason());
        }

        Date now = new Date();
        tournament.setModerationStatus(TournamentModerationStatus.DEACTIVATED);
        tournament.setModeratedAt(now);
        tournament.setModeratedByAdminId(admin.getId());
        tournament.setModerationReason(reason.trim());

        tournamentRepositoryPort.update(tournament);

        try {
            notificationPort.notifyUsersOfTournament(
                    tournamentId,
                    "Torneo dado de baja",
                    "El torneo '" + tournament.getName() + "' fue dado de baja por moderación. No recibirá más actualizaciones.",
                    NotificationType.TOURNAMENT_DEACTIVATED_BY_ADMIN);
        } catch (Exception e) {
            System.err.println("Error enviando notificación de desactivación: " + e.getMessage());
        }

        return new DeactivateResult(
                tournamentId,
                TournamentModerationStatus.DEACTIVATED,
                now,
                admin.getId(),
                tournament.getModerationReason());
    }
}

