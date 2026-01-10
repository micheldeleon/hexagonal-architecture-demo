package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.RaceResult;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.out.RaceResultPersistencePort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class ReportRaceResultsUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private RaceResultPersistencePort raceResultPersistencePort;
    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    void report_validatesInputs() {
        ReportRaceResultsUseCase useCase = new ReportRaceResultsUseCase(tournamentRepositoryPort, raceResultPersistencePort,
                userRepositoryPort);

        assertThatThrownBy(() -> useCase.report(null, "org@example.com", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.report(1L, "org@example.com", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.report(1L, " ", List.of(new RaceResult())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void report_rejectsDuplicatesOrMissingTeamsOrInvalidTimes() {
        ReportRaceResultsUseCase useCase = new ReportRaceResultsUseCase(tournamentRepositoryPort, raceResultPersistencePort,
                userRepositoryPort);

        User organizer = TestDataFactory.validUser(1L);
        organizer.setEmail("org@example.com");
        when(userRepositoryPort.findByEmail("org@example.com")).thenReturn(Optional.of(organizer));

        Tournament tournament = TestDataFactory.startedRaceTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);
        when(raceResultPersistencePort.getTeamIdsForTournament(10L)).thenReturn(List.of(1L, 2L));

        RaceResult r1 = new RaceResult(null, 10L, 1L, null, 1000L, null, null, null);
        RaceResult dup = new RaceResult(null, 10L, 1L, null, 1200L, null, null, null);
        assertThatThrownBy(() -> useCase.report(10L, "org@example.com", List.of(r1, dup)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("duplicados");

        RaceResult missing = new RaceResult(null, 10L, 1L, null, 1000L, null, null, null);
        assertThatThrownBy(() -> useCase.report(10L, "org@example.com", List.of(missing)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exactamente");

        RaceResult badTime = new RaceResult(null, 10L, 2L, null, 0L, null, null, null);
        assertThatThrownBy(() -> useCase.report(10L, "org@example.com", List.of(r1, badTime)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tiempo");
    }

    @Test
    void report_success_ordersByTimeAndSetsPositions() {
        ReportRaceResultsUseCase useCase = new ReportRaceResultsUseCase(tournamentRepositoryPort, raceResultPersistencePort,
                userRepositoryPort);

        User organizer = TestDataFactory.validUser(1L);
        organizer.setEmail("org@example.com");
        when(userRepositoryPort.findByEmail("org@example.com")).thenReturn(Optional.of(organizer));

        Tournament tournament = TestDataFactory.startedRaceTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);
        when(raceResultPersistencePort.getTeamIdsForTournament(10L)).thenReturn(List.of(1L, 2L, 3L));

        RaceResult r1 = new RaceResult(null, 10L, 1L, null, 2000L, null, null, null);
        RaceResult r2 = new RaceResult(null, 10L, 2L, null, 1000L, null, null, null);
        RaceResult r3 = new RaceResult(null, 10L, 3L, null, 1000L, null, null, null); // tie -> teamId

        useCase.report(10L, "org@example.com", List.of(r1, r2, r3));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RaceResult>> captor = ArgumentCaptor.forClass(List.class);
        verify(raceResultPersistencePort).replaceResults(eq(10L), captor.capture());

        List<RaceResult> saved = captor.getValue();
        assertThat(saved).hasSize(3);
        assertThat(saved.get(0).getTeamId()).isEqualTo(2L);
        assertThat(saved.get(0).getPosition()).isEqualTo(1);
        assertThat(saved.get(1).getTeamId()).isEqualTo(3L);
        assertThat(saved.get(1).getPosition()).isEqualTo(2);
        assertThat(saved.get(2).getTeamId()).isEqualTo(1L);
        assertThat(saved.get(2).getPosition()).isEqualTo(3);
        assertThat(saved).allMatch(rr -> rr.getCreatedAt() != null && rr.getUpdatedAt() != null);
    }
}

