package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Participant;
import com.example.demo.core.domain.models.Team;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.ports.out.TournamentRepositoryPort;

@ExtendWith(MockitoExtension.class)
class GetTournamentUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;

    @Test
    void getSubscribedTournaments_throwsWhenRepositoryReturnsNull() {
        GetTournamentUseCase useCase = new GetTournamentUseCase(tournamentRepositoryPort);
        when(tournamentRepositoryPort.findAllWithTeams()).thenReturn(null);
        assertThatThrownBy(() -> useCase.getSubscribedTournaments("12345678"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No hay torneos");
    }

    @Test
    void getSubscribedTournaments_filtersByNationalIdAcrossTeams() {
        GetTournamentUseCase useCase = new GetTournamentUseCase(tournamentRepositoryPort);

        Participant p = new Participant();
        p.setNationalId("12345678");
        Team team = new Team();
        team.setParticipants(List.of(p));
        Tournament t1 = new Tournament();
        t1.setId(1L);
        t1.setTeams(List.of(team));

        Tournament t2 = new Tournament();
        t2.setId(2L);
        t2.setTeams(List.of());

        when(tournamentRepositoryPort.findAllWithTeams()).thenReturn(List.of(t1, t2));

        assertThat(useCase.getSubscribedTournaments("12345678"))
                .extracting(Tournament::getId)
                .containsExactly(1L);
    }
}

