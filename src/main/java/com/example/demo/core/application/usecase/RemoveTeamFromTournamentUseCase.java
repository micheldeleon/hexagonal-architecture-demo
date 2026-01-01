package com.example.demo.core.application.usecase;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.ports.in.RemoveTeamFromTournamentPort;
import com.example.demo.core.ports.out.TeamRemovalPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;

public class RemoveTeamFromTournamentUseCase implements RemoveTeamFromTournamentPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final TeamRemovalPort teamRemovalPort;

    public RemoveTeamFromTournamentUseCase(TournamentRepositoryPort tournamentRepositoryPort,
            TeamRemovalPort teamRemovalPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.teamRemovalPort = teamRemovalPort;
    }

    @Override
    public void removeTeam(Long tournamentId, Long organizerId, Long teamId, String comment) {
        // 1. Verificar que el torneo existe
        Tournament tournament = tournamentRepositoryPort.findByIdWithTeams(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }

        // 2. Verificar que el usuario es el organizador del torneo
        if (!tournament.getOrganizer().getId().equals(organizerId)) {
            throw new IllegalStateException("Solo el organizador puede eliminar equipos del torneo");
        }

        // 3. Verificar que el torneo está en estado ABIERTO
        if (tournament.getStatus() != TournamentStatus.ABIERTO) {
            throw new IllegalStateException("Solo se pueden eliminar equipos de torneos en estado ABIERTO");
        }

        // 4. Verificar que el equipo está inscrito en el torneo
        boolean teamExists = tournament.getTeams().stream()
                .anyMatch(team -> team.getId().equals(teamId));

        if (!teamExists) {
            throw new IllegalArgumentException("El equipo no está inscrito en este torneo");
        }

        // 5. Eliminar el equipo del torneo
        teamRemovalPort.removeTeamFromTournament(tournamentId, teamId);

        // 6. Decrementar el contador de equipos inscritos
        int current = tournament.getTeamsInscribed();
        tournament.setTeamsInscribed(Math.max(0, current - 1));

        // 7. Guardar los cambios del torneo
        tournamentRepositoryPort.save(tournament, organizerId);

        // Nota: El comentario se podría guardar en una tabla de auditoría o logs
        // Por ahora, simplemente lo incluimos como parámetro para futura implementación
        if (comment != null && !comment.isBlank()) {
            // Aquí se podría implementar un sistema de logging o auditoría
            System.out.println("Equipo eliminado por: " + organizerId + " - Razón: " + comment);
        }
    }
}
