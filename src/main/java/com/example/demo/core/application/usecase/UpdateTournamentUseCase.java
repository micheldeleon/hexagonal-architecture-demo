package com.example.demo.core.application.usecase;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.ports.in.UpdateTournamentPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.NotificationPort;

public class UpdateTournamentUseCase implements UpdateTournamentPort {

    private final TournamentRepositoryPort tournamentRepository;
    private final NotificationPort notificationPort;

    public UpdateTournamentUseCase(TournamentRepositoryPort tournamentRepository, NotificationPort notificationPort) {
        this.tournamentRepository = tournamentRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    @Transactional
    public Tournament update(Tournament tournament) {
        if (tournament == null || tournament.getId() == null) {
            throw new IllegalArgumentException("Tournament must not be null and must have an ID");
        }
        
        // Obtener el torneo actual
        Tournament existingTournament = tournamentRepository.findById(tournament.getId());
        if (existingTournament == null) {
            throw new IllegalArgumentException("Tournament with id " + tournament.getId() + " not found");
        }
        if (existingTournament.getModerationStatus() == TournamentModerationStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }

        // Validar que el torneo esté ABIERTO
        if (existingTournament.getStatus() != TournamentStatus.ABIERTO) {
            throw new IllegalStateException("Only ABIERTO tournaments can be updated. Current status: " + existingTournament.getStatus());
        }

        // Validar restricciones de precio y premio si hay equipos inscritos
        boolean hasTeams = existingTournament.getTeamsInscribed() > 0;
        
        if (hasTeams) {
            // Si hay equipos inscritos, no permitir cambiar precio ni premio
            if (tournament.getRegistrationCost() != existingTournament.getRegistrationCost()) {
                throw new IllegalStateException("Cannot modify registration cost when teams are already registered");
            }
            if (!isSameString(tournament.getPrize(), existingTournament.getPrize())) {
                throw new IllegalStateException("Cannot modify prize when teams are already registered");
            }
        }

        // Validar el torneo actualizado
        tournament.validate();

        // Guardar cambios
        Tournament updatedTournament = tournamentRepository.update(tournament);

        // Notificar a los participantes inscritos si hay cambios (en transacción separada)
        if (hasTeams) {
            try {
                notificationPort.notifyUsersOfTournament(
                    updatedTournament.getId(),
                    "Torneo Actualizado",
                    "El torneo \"" + updatedTournament.getName() + "\" ha sido actualizado. Revisa los nuevos detalles.",
                    NotificationType.TOURNAMENT_UPDATED
                );
            } catch (Exception e) {
                // Log pero no fallar - las notificaciones son secundarias
                System.err.println("Error enviando notificaciones: " + e.getMessage());
            }
        }

        return updatedTournament;
    }

    private boolean isSameString(String s1, String s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return s1.equals(s2);
    }
}
