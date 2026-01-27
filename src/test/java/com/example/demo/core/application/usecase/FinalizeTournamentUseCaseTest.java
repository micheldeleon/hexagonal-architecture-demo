package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.RaceResult;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.ports.out.FixturePersistencePort;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.RaceResultPersistencePort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class FinalizeTournamentUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private FixturePersistencePort fixturePersistencePort;
    @Mock
    private RaceResultPersistencePort raceResultPersistencePort;
        @Mock
        private NotificationPort notificationPort;

    @Test
    void finalizeTournament_validatesInputs() {
        FinalizeTournamentUseCase useCase = new FinalizeTournamentUseCase(
                tournamentRepositoryPort, fixturePersistencePort, raceResultPersistencePort, notificationPort);

        assertThatThrownBy(() -> useCase.finalizeTournament(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tournamentId es requerido");
    }

    @Test
    void finalizeTournament_rejectsTournamentNotFound() {
        FinalizeTournamentUseCase useCase = new FinalizeTournamentUseCase(
                tournamentRepositoryPort, fixturePersistencePort, raceResultPersistencePort, notificationPort);

        when(tournamentRepositoryPort.findById(10L)).thenReturn(null);

        assertThatThrownBy(() -> useCase.finalizeTournament(10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Torneo no encontrado");
    }

    @Test
    void finalizeTournament_rejectsTournamentNotStarted() {
        FinalizeTournamentUseCase useCase = new FinalizeTournamentUseCase(
                tournamentRepositoryPort, fixturePersistencePort, raceResultPersistencePort, notificationPort);

        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        tournament.setStatus(TournamentStatus.ABIERTO); // Not started
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        assertThatThrownBy(() -> useCase.finalizeTournament(10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no está en estado INICIADO");
    }

    @Test
    void finalizeTournament_rejectsEliminationWithUnfinishedMatches() {
        FinalizeTournamentUseCase useCase = new FinalizeTournamentUseCase(
                tournamentRepositoryPort, fixturePersistencePort, raceResultPersistencePort, notificationPort);

        Tournament tournament = TestDataFactory.startedEliminationTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        TournamentMatch pendingMatch = new TournamentMatch();
        pendingMatch.setId(100L);
        pendingMatch.setTournamentId(10L);
        pendingMatch.setRound(1);
        pendingMatch.setMatchNumber(1);
        pendingMatch.setStatus("PENDING"); // Not finished
        when(fixturePersistencePort.findByTournament(10L)).thenReturn(List.of(pendingMatch));

        assertThatThrownBy(() -> useCase.finalizeTournament(10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene resultado");
    }

    @Test
    void finalizeTournament_rejectsLeagueWithMissingScores() {
        FinalizeTournamentUseCase useCase = new FinalizeTournamentUseCase(
                tournamentRepositoryPort, fixturePersistencePort, raceResultPersistencePort, notificationPort);

        Tournament tournament = TestDataFactory.startedLeagueTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        TournamentMatch matchWithoutScores = new TournamentMatch();
        matchWithoutScores.setId(100L);
        matchWithoutScores.setTournamentId(10L);
        matchWithoutScores.setRound(1);
        matchWithoutScores.setMatchNumber(1);
        matchWithoutScores.setStatus("FINISHED");
        matchWithoutScores.setScoreHome(null); // Missing score
        matchWithoutScores.setScoreAway(null);
        when(fixturePersistencePort.findByTournament(10L)).thenReturn(List.of(matchWithoutScores));

        assertThatThrownBy(() -> useCase.finalizeTournament(10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene los puntajes establecidos");
    }

    @Test
    void finalizeTournament_rejectsRaceWithNoResults() {
        FinalizeTournamentUseCase useCase = new FinalizeTournamentUseCase(
                tournamentRepositoryPort, fixturePersistencePort, raceResultPersistencePort, notificationPort);

        Tournament tournament = TestDataFactory.startedRaceTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);
        when(raceResultPersistencePort.findByTournament(10L)).thenReturn(List.of()); // No results

        assertThatThrownBy(() -> useCase.finalizeTournament(10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No hay resultados de carrera");
    }

    @Test
    void finalizeTournament_rejectsRaceWithMissingPositions() {
        FinalizeTournamentUseCase useCase = new FinalizeTournamentUseCase(
                tournamentRepositoryPort, fixturePersistencePort, raceResultPersistencePort, notificationPort);

        Tournament tournament = TestDataFactory.startedRaceTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        RaceResult result = new RaceResult();
        result.setId(1L);
        result.setTournamentId(10L);
        result.setTeamId(1L);
        result.setPosition(null); // Missing position
        result.setTimeMillis(1000L);
        when(raceResultPersistencePort.findByTournament(10L)).thenReturn(List.of(result));

        assertThatThrownBy(() -> useCase.finalizeTournament(10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene posición asignada");
    }

    @Test
    void finalizeTournament_successWithAllMatchesFinished() {
        FinalizeTournamentUseCase useCase = new FinalizeTournamentUseCase(
                tournamentRepositoryPort, fixturePersistencePort, raceResultPersistencePort, notificationPort);

        Tournament tournament = TestDataFactory.startedEliminationTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        TournamentMatch finishedMatch = new TournamentMatch();
        finishedMatch.setId(100L);
        finishedMatch.setTournamentId(10L);
        finishedMatch.setRound(1);
        finishedMatch.setMatchNumber(1);
        finishedMatch.setStatus("FINISHED");
        finishedMatch.setScoreHome(2);
        finishedMatch.setScoreAway(1);
        finishedMatch.setWinnerTeamId(1L);
        when(fixturePersistencePort.findByTournament(10L)).thenReturn(List.of(finishedMatch));

        useCase.finalizeTournament(10L);

        ArgumentCaptor<Tournament> captor = ArgumentCaptor.forClass(Tournament.class);
        verify(tournamentRepositoryPort).save(captor.capture(), eq(1L));
        
        Tournament savedTournament = captor.getValue();
        assertThat(savedTournament.getStatus()).isEqualTo(TournamentStatus.FINALIZADO);
        assertThat(savedTournament.getEndAt()).isNotNull();
    }

    @Test
    void finalizeTournament_successWithRaceResults() {
        FinalizeTournamentUseCase useCase = new FinalizeTournamentUseCase(
                tournamentRepositoryPort, fixturePersistencePort, raceResultPersistencePort, notificationPort);

        Tournament tournament = TestDataFactory.startedRaceTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        RaceResult result1 = new RaceResult();
        result1.setId(1L);
        result1.setTournamentId(10L);
        result1.setTeamId(1L);
        result1.setPosition(1);
        result1.setTimeMillis(1000L);

        RaceResult result2 = new RaceResult();
        result2.setId(2L);
        result2.setTournamentId(10L);
        result2.setTeamId(2L);
        result2.setPosition(2);
        result2.setTimeMillis(1200L);

        when(raceResultPersistencePort.findByTournament(10L)).thenReturn(List.of(result1, result2));

        useCase.finalizeTournament(10L);

        ArgumentCaptor<Tournament> captor = ArgumentCaptor.forClass(Tournament.class);
        verify(tournamentRepositoryPort).save(captor.capture(), eq(1L));
        
        Tournament savedTournament = captor.getValue();
        assertThat(savedTournament.getStatus()).isEqualTo(TournamentStatus.FINALIZADO);
        assertThat(savedTournament.getEndAt()).isNotNull();
    }
}
