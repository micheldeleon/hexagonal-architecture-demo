package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Formats.LeagueFormat;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.ports.in.models.LeagueStanding;
import com.example.demo.core.ports.out.FixturePersistencePort;
import com.example.demo.core.ports.out.TeamQueryPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class GetLeagueStandingsUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private FixturePersistencePort fixturePersistencePort;
    @Mock
    private TeamQueryPort teamQueryPort;

    @Test
    void list_validatesTournamentAndFormat() {
        GetLeagueStandingsUseCase useCase = new GetLeagueStandingsUseCase(tournamentRepositoryPort, fixturePersistencePort,
                teamQueryPort);

        assertThatThrownBy(() -> useCase.list(null)).isInstanceOf(IllegalArgumentException.class);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(null);
        assertThatThrownBy(() -> useCase.list(10L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void list_computesTableAndSortsByPointsThenGoalDifference() {
        GetLeagueStandingsUseCase useCase = new GetLeagueStandingsUseCase(tournamentRepositoryPort, fixturePersistencePort,
                teamQueryPort);

        Tournament tournament = TestDataFactory.startedLeagueTournament(10L, 1L);
        LeagueFormat leagueFormat = new LeagueFormat(3, 1, 0, true);
        tournament.setFormat(leagueFormat);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        when(teamQueryPort.findTeamsByTournament(10L)).thenReturn(List.of(
                new TeamQueryPort.TeamSummary(1L, "A"),
                new TeamQueryPort.TeamSummary(2L, "B"),
                new TeamQueryPort.TeamSummary(3L, "C")));

        TournamentMatch aBeatsB = new TournamentMatch();
        aBeatsB.setTournamentId(10L);
        aBeatsB.setStatus("FINISHED");
        aBeatsB.setHomeTeamId(1L);
        aBeatsB.setAwayTeamId(2L);
        aBeatsB.setScoreHome(2);
        aBeatsB.setScoreAway(0);

        TournamentMatch bDrawsC = new TournamentMatch();
        bDrawsC.setTournamentId(10L);
        bDrawsC.setStatus("FINISHED");
        bDrawsC.setHomeTeamId(2L);
        bDrawsC.setAwayTeamId(3L);
        bDrawsC.setScoreHome(1);
        bDrawsC.setScoreAway(1);

        when(fixturePersistencePort.findByTournament(10L)).thenReturn(List.of(aBeatsB, bDrawsC));

        List<LeagueStanding> standings = useCase.list(10L);
        assertThat(standings).hasSize(3);

        // A: win (3 pts), B: draw (1) + loss (0) = 1, C: draw (1)
        assertThat(standings.get(0).teamId()).isEqualTo(1L);
        assertThat(standings.get(0).points()).isEqualTo(3);

        // B and C both 1pt: tie-break by goal difference, then goals for, then teamId
        assertThat(standings.get(1).points()).isEqualTo(1);
        assertThat(standings.get(2).points()).isEqualTo(1);
        assertThat(standings.get(1).goalDifference()).isGreaterThanOrEqualTo(standings.get(2).goalDifference());
    }
}

