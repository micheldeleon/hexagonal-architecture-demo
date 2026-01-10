package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Formats.EliminationFormat;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.ports.out.FixturePersistencePort;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class ReportMatchResultUseCaseTest {

    @Mock
    private FixturePersistencePort fixturePersistencePort;
    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private NotificationPort notificationPort;

    @Test
    void reportResult_validatesWinnerAndNoDrawsInElimination() {
        ReportMatchResultUseCase useCase = new ReportMatchResultUseCase(fixturePersistencePort, tournamentRepositoryPort,
                notificationPort);

        TournamentMatch match = new TournamentMatch();
        match.setId(100L);
        match.setTournamentId(10L);
        match.setRound(1);
        match.setMatchNumber(1);
        match.setHomeTeamId(1L);
        match.setAwayTeamId(2L);
        when(fixturePersistencePort.getMatch(100L)).thenReturn(match);

        Tournament tournament = TestDataFactory.startedEliminationTournament(10L, 1L);
        tournament.setFormat(new EliminationFormat());
        tournament.setStatus(TournamentStatus.INICIADO);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        assertThatThrownBy(() -> useCase.reportResult(10L, 100L, 1, 1, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empates");

        assertThatThrownBy(() -> useCase.reportResult(10L, 100L, 0, 1, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no coincide");
    }

    @Test
    void reportResult_updatesMatchAndAdvancesWinnerToNextRound() {
        ReportMatchResultUseCase useCase = new ReportMatchResultUseCase(fixturePersistencePort, tournamentRepositoryPort,
                notificationPort);

        TournamentMatch match = new TournamentMatch();
        match.setId(100L);
        match.setTournamentId(10L);
        match.setRound(1);
        match.setMatchNumber(2); // even -> winner goes as away
        match.setHomeTeamId(1L);
        match.setAwayTeamId(2L);
        match.setWinnerTeamId(1L); // previous winner
        match.setStatus("PENDING");
        match.setCreatedAt(new Date(0));
        when(fixturePersistencePort.getMatch(100L)).thenReturn(match);
        when(fixturePersistencePort.countByTournamentAndRound(10L, 1)).thenReturn(2);

        TournamentMatch next = new TournamentMatch();
        next.setTournamentId(10L);
        next.setRound(2);
        next.setMatchNumber(1);
        next.setStatus("FINISHED");
        next.setScoreHome(9);
        next.setScoreAway(0);
        next.setWinnerTeamId(1L);
        when(fixturePersistencePort.findByTournamentRoundAndNumber(10L, 2, 1)).thenReturn(next);

        Tournament tournament = TestDataFactory.startedEliminationTournament(10L, 1L);
        tournament.setFormat(new EliminationFormat());
        tournament.setStatus(TournamentStatus.INICIADO);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        useCase.reportResult(10L, 100L, 0, 1, 2L);

        ArgumentCaptor<TournamentMatch> captor = ArgumentCaptor.forClass(TournamentMatch.class);
        verify(fixturePersistencePort, times(2)).saveMatch(captor.capture());

        TournamentMatch savedMatch = captor.getAllValues().get(0);
        assertThat(savedMatch.getStatus()).isEqualTo("FINISHED");
        assertThat(savedMatch.getWinnerTeamId()).isEqualTo(2L);

        TournamentMatch updatedNext = captor.getAllValues().get(1);
        assertThat(updatedNext.getRound()).isEqualTo(2);
        assertThat(updatedNext.getMatchNumber()).isEqualTo(1);
        assertThat(updatedNext.getAwayTeamId()).isEqualTo(2L);
        assertThat(updatedNext.getStatus()).isEqualTo("PENDING"); // winner changed reopens
        assertThat(updatedNext.getWinnerTeamId()).isNull();
        assertThat(updatedNext.getScoreHome()).isNull();

        verify(notificationPort).notifyTeamMembers(eq(1L), any(), any(), eq(NotificationType.MATCH_RESULT));
        verify(notificationPort).notifyTeamMembers(eq(2L), any(), any(), eq(NotificationType.MATCH_RESULT));
    }
}
