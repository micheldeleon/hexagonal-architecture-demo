package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Formats.LeagueFormat;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.ports.out.FixturePersistencePort;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class ReportLeagueMatchResultUseCaseTest {

    @Mock
    private FixturePersistencePort fixturePersistencePort;
    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private NotificationPort notificationPort;

    @Test
    void reportResult_validatesInputsAndTournamentState() {
        ReportLeagueMatchResultUseCase useCase = new ReportLeagueMatchResultUseCase(fixturePersistencePort,
                tournamentRepositoryPort, notificationPort);

        assertThatThrownBy(() -> useCase.reportResult(null, 1L, 1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.reportResult(1L, null, 1, 0)).isInstanceOf(IllegalArgumentException.class);

        when(fixturePersistencePort.getMatch(100L)).thenReturn(null);
        assertThatThrownBy(() -> useCase.reportResult(10L, 100L, 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reportResult_updatesMatchAndAllowsDraw() {
        ReportLeagueMatchResultUseCase useCase = new ReportLeagueMatchResultUseCase(fixturePersistencePort,
                tournamentRepositoryPort, notificationPort);

        TournamentMatch match = new TournamentMatch();
        match.setId(100L);
        match.setTournamentId(10L);
        match.setHomeTeamId(1L);
        match.setAwayTeamId(2L);
        match.setCreatedAt(new Date(0));
        when(fixturePersistencePort.getMatch(100L)).thenReturn(match);

        Tournament tournament = TestDataFactory.startedLeagueTournament(10L, 1L);
        tournament.setFormat(new LeagueFormat(3, 1, 0, true));
        tournament.setStatus(TournamentStatus.INICIADO);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        useCase.reportResult(10L, 100L, 2, 2);

        ArgumentCaptor<TournamentMatch> captor = ArgumentCaptor.forClass(TournamentMatch.class);
        verify(fixturePersistencePort).saveMatch(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("FINISHED");
        assertThat(captor.getValue().getWinnerTeamId()).isNull();

        verify(notificationPort).notifyTeamMembers(eq(1L), any(), any(), eq(NotificationType.MATCH_RESULT));
        verify(notificationPort).notifyTeamMembers(eq(2L), any(), any(), eq(NotificationType.MATCH_RESULT));
    }
}

