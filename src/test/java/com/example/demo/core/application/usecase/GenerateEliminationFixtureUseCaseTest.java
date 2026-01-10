package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
class GenerateEliminationFixtureUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private FixturePersistencePort fixturePersistencePort;
    @Mock
    private NotificationPort notificationPort;

    @Test
    void generate_validatesPreconditions() {
        GenerateEliminationFixtureUseCase useCase = new GenerateEliminationFixtureUseCase(tournamentRepositoryPort,
                fixturePersistencePort, notificationPort);

        assertThatThrownBy(() -> useCase.generate(null)).isInstanceOf(IllegalArgumentException.class);

        when(tournamentRepositoryPort.findById(10L)).thenReturn(null);
        assertThatThrownBy(() -> useCase.generate(10L)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Torneo no encontrado");
    }

    @Test
    void generate_rejectsWrongStatusOrFormatOrAlreadyGeneratedOrTooFewTeams() {
        GenerateEliminationFixtureUseCase useCase = new GenerateEliminationFixtureUseCase(tournamentRepositoryPort,
                fixturePersistencePort, notificationPort);

        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        tournament.setFormat(new EliminationFormat());
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        assertThatThrownBy(() -> useCase.generate(10L)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("INICIADO");

        tournament.setStatus(TournamentStatus.INICIADO);
        tournament.setFormat(new com.example.demo.core.domain.models.SimpleFormat(1L, "Liga", true));
        assertThatThrownBy(() -> useCase.generate(10L)).isInstanceOf(IllegalStateException.class);

        tournament.setFormat(new EliminationFormat());
        when(fixturePersistencePort.hasMatches(10L)).thenReturn(true);
        assertThatThrownBy(() -> useCase.generate(10L)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya fue generado");

        when(fixturePersistencePort.hasMatches(10L)).thenReturn(false);
        when(fixturePersistencePort.getTeamIdsForTournament(10L)).thenReturn(List.of(1L));
        assertThatThrownBy(() -> useCase.generate(10L)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("al menos 2");

        verify(fixturePersistencePort, never()).saveMatches(any());
    }

    @Test
    void generate_buildsBracketWithByesAndSavesMatches() {
        GenerateEliminationFixtureUseCase useCase = new GenerateEliminationFixtureUseCase(tournamentRepositoryPort,
                fixturePersistencePort, notificationPort);

        Tournament tournament = TestDataFactory.startedEliminationTournament(10L, 1L);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);
        when(fixturePersistencePort.hasMatches(10L)).thenReturn(false);
        when(fixturePersistencePort.getTeamIdsForTournament(10L))
                .thenReturn(new java.util.ArrayList<>(List.of(1L, 2L, 3L))); // needs 1 bye (mutable for shuffle)

        useCase.generate(10L);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TournamentMatch>> matchesCaptor = ArgumentCaptor.forClass(List.class);
        verify(fixturePersistencePort).saveMatches(matchesCaptor.capture());

        List<TournamentMatch> matches = matchesCaptor.getValue();
        assertThat(matches).hasSize(3); // 4 slots => 2 in round 1 + 1 final
        assertThat(matches).allMatch(m -> m.getTournamentId().equals(10L));

        long round1ByeMatches = matches.stream()
                .filter(m -> m.getRound() == 1)
                .filter(m -> (m.getHomeTeamId() == null) ^ (m.getAwayTeamId() == null))
                .count();
        assertThat(round1ByeMatches).isEqualTo(1);

        matches.stream()
                .filter(m -> m.getRound() == 1)
                .filter(m -> (m.getHomeTeamId() == null) ^ (m.getAwayTeamId() == null))
                .forEach(m -> {
                    assertThat(m.getStatus()).isEqualTo("FINISHED");
                    assertThat(m.getWinnerTeamId()).isNotNull();
                });

        verify(notificationPort).notifyUsersOfTournament(eq(10L), any(), any(), eq(NotificationType.MATCH_SCHEDULED));
    }
}
