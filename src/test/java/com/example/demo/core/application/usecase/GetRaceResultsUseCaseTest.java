package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.RaceResult;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.ports.out.RaceResultPersistencePort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class GetRaceResultsUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private RaceResultPersistencePort raceResultPersistencePort;

    @Test
    void list_validatesTournamentAndFormat() {
        GetRaceResultsUseCase useCase = new GetRaceResultsUseCase(tournamentRepositoryPort, raceResultPersistencePort);

        assertThatThrownBy(() -> useCase.list(null)).isInstanceOf(IllegalArgumentException.class);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(null);
        assertThatThrownBy(() -> useCase.list(10L)).isInstanceOf(IllegalArgumentException.class);

        Tournament notRace = TestDataFactory.startedEliminationTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(notRace);
        assertThatThrownBy(() -> useCase.list(10L)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void list_returnsPersistedResultsWhenPresentOtherwiseRegisteredTeams() {
        GetRaceResultsUseCase useCase = new GetRaceResultsUseCase(tournamentRepositoryPort, raceResultPersistencePort);

        Tournament tournament = TestDataFactory.startedRaceTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        RaceResult persisted = new RaceResult();
        persisted.setTeamId(1L);
        when(raceResultPersistencePort.findByTournament(10L)).thenReturn(List.of(persisted));

        assertThat(useCase.list(10L)).containsExactly(persisted);

        when(raceResultPersistencePort.findByTournament(10L)).thenReturn(List.of());
        RaceResult placeholder = new RaceResult();
        placeholder.setTeamId(2L);
        when(raceResultPersistencePort.findRegisteredTeams(10L)).thenReturn(List.of(placeholder));

        assertThat(useCase.list(10L)).containsExactly(placeholder);
    }
}

