package com.tutorneo.adapters.out.persistence.jpa.mappers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import com.tutorneo.adapters.out.persistence.jpa.entities.TournamentMatchEntity;
import com.tutorneo.core.domain.models.TournamentMatch;

public class TournamentMatchMapper {

    private TournamentMatchMapper() {
    }

    public static TournamentMatchEntity toEntity(TournamentMatch match) {
        TournamentMatchEntity entity = new TournamentMatchEntity();
        entity.setId(match.getId());
        entity.setTournamentId(match.getTournamentId());
        entity.setRound(match.getRound());
        entity.setMatchNumber(match.getMatchNumber());
        entity.setHomeTeamId(match.getHomeTeamId());
        entity.setAwayTeamId(match.getAwayTeamId());
        entity.setScheduledAt(toOdt(match.getScheduledAt()));
        entity.setStatus(match.getStatus());
        entity.setScoreHome(match.getScoreHome());
        entity.setScoreAway(match.getScoreAway());
        entity.setWinnerTeamId(match.getWinnerTeamId());
        entity.setCreatedAt(toOdt(match.getCreatedAt()));
        entity.setUpdatedAt(toOdt(match.getUpdatedAt()));
        return entity;
    }

    public static TournamentMatch toDomain(TournamentMatchEntity entity) {
        TournamentMatch match = new TournamentMatch();
        match.setId(entity.getId());
        match.setTournamentId(entity.getTournamentId());
        match.setRound(entity.getRound());
        match.setMatchNumber(entity.getMatchNumber());
        match.setHomeTeamId(entity.getHomeTeamId());
        match.setAwayTeamId(entity.getAwayTeamId());
        match.setScheduledAt(fromOdt(entity.getScheduledAt()));
        match.setStatus(entity.getStatus());
        match.setScoreHome(entity.getScoreHome());
        match.setScoreAway(entity.getScoreAway());
        match.setWinnerTeamId(entity.getWinnerTeamId());
        match.setCreatedAt(fromOdt(entity.getCreatedAt()));
        match.setUpdatedAt(fromOdt(entity.getUpdatedAt()));
        return match;
    }

    private static OffsetDateTime toOdt(Date date) {
        return date == null ? null : date.toInstant().atOffset(ZoneOffset.UTC);
    }

    private static Date fromOdt(OffsetDateTime odt) {
        return odt == null ? null : Date.from(odt.toInstant());
    }
}
