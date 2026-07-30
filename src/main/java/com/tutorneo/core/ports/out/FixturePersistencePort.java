package com.tutorneo.core.ports.out;

import java.util.List;

import com.tutorneo.core.domain.models.TournamentMatch;

public interface FixturePersistencePort {

    boolean hasMatches(Long tournamentId);

    List<Long> getTeamIdsForTournament(Long tournamentId);

    void saveMatches(List<TournamentMatch> matches);

    List<TournamentMatch> findByTournament(Long tournamentId);

    TournamentMatch getMatch(Long matchId);

    void saveMatch(TournamentMatch match);

    TournamentMatch findByTournamentRoundAndNumber(Long tournamentId, int round, int matchNumber);

    int countByTournamentAndRound(Long tournamentId, int round);
}
