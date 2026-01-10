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
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.Formats.LeagueFormat;
import com.example.demo.core.ports.out.FixturePersistencePort;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class GenerateLeagueFixtureUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private FixturePersistencePort fixturePersistencePort;
    @Mock
    private NotificationPort notificationPort;

    @Test
    void generate_rejectsInvalidTournamentOrFormatOrStatus() {
        GenerateLeagueFixtureUseCase useCase = new GenerateLeagueFixtureUseCase(tournamentRepositoryPort,
                fixturePersistencePort, notificationPort);

        assertThatThrownBy(() -> useCase.generate(null, false)).isInstanceOf(IllegalArgumentException.class);

        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        tournament.setFormat(new LeagueFormat());
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        assertThatThrownBy(() -> useCase.generate(10L, false)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("INICIADO");

        tournament.setStatus(TournamentStatus.INICIADO);
        tournament.setFormat(new com.example.demo.core.domain.models.SimpleFormat(1L, "Eliminatorio", true));
        assertThatThrownBy(() -> useCase.generate(10L, false)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void generate_buildsRoundRobinAndDoubleRound() {
        GenerateLeagueFixtureUseCase useCase = new GenerateLeagueFixtureUseCase(tournamentRepositoryPort,
                fixturePersistencePort, notificationPort);

        Tournament tournament = TestDataFactory.startedLeagueTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);
        when(fixturePersistencePort.hasMatches(10L)).thenReturn(false);
        when(fixturePersistencePort.getTeamIdsForTournament(10L)).thenReturn(List.of(1L, 2L, 3L, 4L));

        useCase.generate(10L, true);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TournamentMatch>> matchesCaptor = ArgumentCaptor.forClass(List.class);
        verify(fixturePersistencePort).saveMatches(matchesCaptor.capture());
        List<TournamentMatch> matches = matchesCaptor.getValue();

        assertThat(matches).hasSize(12); // 4 teams: 6 matches per round robin, x2
        assertThat(matches).allMatch(m -> "PENDING".equals(m.getStatus()));
        assertThat(matches.stream().map(TournamentMatch::getRound).distinct()).containsExactlyInAnyOrder(1, 2, 3, 4,
                5, 6);

        verify(notificationPort).notifyUsersOfTournament(eq(10L), any(), any(), eq(NotificationType.MATCH_SCHEDULED));
    }
}

