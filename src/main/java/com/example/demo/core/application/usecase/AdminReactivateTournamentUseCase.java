package com.example.demo.core.application.usecase;

import java.util.Date;

import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.AdminReactivateTournamentPort;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

public class AdminReactivateTournamentUseCase implements AdminReactivateTournamentPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final NotificationPort notificationPort;

    public AdminReactivateTournamentUseCase(
            TournamentRepositoryPort tournamentRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            NotificationPort notificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public ReactivateResult reactivate(Long tournamentId, String adminEmail, String reason) {
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

        if (tournament.getModerationStatus() == TournamentModerationStatus.ACTIVE) {
            return new ReactivateResult(
                    tournamentId,
                    TournamentModerationStatus.ACTIVE,
                    tournament.getModeratedAt(),
                    tournament.getModeratedByAdminId(),
                    tournament.getModerationReason());
        }

        Date now = new Date();
        tournament.setModerationStatus(TournamentModerationStatus.ACTIVE);
        tournament.setModeratedAt(now);
        tournament.setModeratedByAdminId(admin.getId());
        tournament.setModerationReason(reason.trim());

        tournamentRepositoryPort.update(tournament);

        try {
            notificationPort.notifyUsersOfTournament(
                    tournamentId,
                    "Torneo reactivado",
                    "El torneo '" + tournament.getName() + "' fue reactivado por moderación.",
                    NotificationType.TOURNAMENT_REACTIVATED_BY_ADMIN);
        } catch (Exception e) {
            System.err.println("Error enviando notificación de reactivación: " + e.getMessage());
        }

        return new ReactivateResult(
                tournamentId,
                TournamentModerationStatus.ACTIVE,
                now,
                admin.getId(),
                tournament.getModerationReason());
    }
}

