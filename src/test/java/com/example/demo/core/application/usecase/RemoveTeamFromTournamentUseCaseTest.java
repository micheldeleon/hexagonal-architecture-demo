package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Team;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TeamRemovalPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class RemoveTeamFromTournamentUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private TeamRemovalPort teamRemovalPort;
    @Mock
    private NotificationPort notificationPort;

    @Test
    void removeTeam_validatesTournamentAndOrganizerAndTeamMembership() {
        RemoveTeamFromTournamentUseCase useCase = new RemoveTeamFromTournamentUseCase(tournamentRepositoryPort,
                teamRemovalPort, notificationPort);

        when(tournamentRepositoryPort.findByIdWithTeams(10L)).thenReturn(null);
        assertThatThrownBy(() -> useCase.removeTeam(10L, 1L, 2L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Torneo no encontrado");

        Tournament tournament = TestDataFactory.baseTournament(10L, 99L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        tournament.setTeams(List.of(new Team(1L, "A", List.of(), null, null)));
        when(tournamentRepositoryPort.findByIdWithTeams(10L)).thenReturn(tournament);

        assertThatThrownBy(() -> useCase.removeTeam(10L, 1L, 1L, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("organizador");

        tournament.getOrganizer().setId(1L);
        tournament.setStatus(TournamentStatus.INICIADO);
        assertThatThrownBy(() -> useCase.removeTeam(10L, 1L, 1L, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ABIERTO");

        tournament.setStatus(TournamentStatus.ABIERTO);
        assertThatThrownBy(() -> useCase.removeTeam(10L, 1L, 2L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no est");
    }

    @Test
    void removeTeam_success_decrementsCounterAndNotifiesTeamMembers() {
        RemoveTeamFromTournamentUseCase useCase = new RemoveTeamFromTournamentUseCase(tournamentRepositoryPort,
                teamRemovalPort, notificationPort);

        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        tournament.setTeamsInscribed(1);
        tournament.setTeams(List.of(new Team(5L, "Equipo", List.of(), null, null)));
        when(tournamentRepositoryPort.findByIdWithTeams(10L)).thenReturn(tournament);

        useCase.removeTeam(10L, 1L, 5L, "faltó fair play");

        verify(teamRemovalPort).removeTeamFromTournament(10L, 5L);

        ArgumentCaptor<Tournament> saved = ArgumentCaptor.forClass(Tournament.class);
        verify(tournamentRepositoryPort).save(saved.capture(), eq(1L));
        assertThat(saved.getValue().getTeamsInscribed()).isEqualTo(0);

        verify(notificationPort).notifyTeamMembers(eq(5L), any(), any(), eq(NotificationType.TEAM_REMOVED));
    }
}

