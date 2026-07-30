package com.tutorneo.adapters.out.persistence.jpa.mappers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import com.tutorneo.adapters.out.persistence.jpa.entities.RaceResultEntity;
import com.tutorneo.core.domain.models.RaceResult;

public class RaceResultMapper {

    private RaceResultMapper() {
    }

    public static RaceResultEntity toEntity(RaceResult result) {
        RaceResultEntity entity = new RaceResultEntity();
        entity.setId(result.getId());
        entity.setTournamentId(result.getTournamentId());
        entity.setTeamId(result.getTeamId());
        entity.setTimeMillis(result.getTimeMillis());
        entity.setPosition(result.getPosition());
        entity.setCreatedAt(toOdt(result.getCreatedAt()));
        entity.setUpdatedAt(toOdt(result.getUpdatedAt()));
        return entity;
    }

    public static RaceResult toDomain(RaceResultEntity entity) {
        RaceResult result = new RaceResult();
        result.setId(entity.getId());
        result.setTournamentId(entity.getTournamentId());
        result.setTeamId(entity.getTeamId());
        result.setTimeMillis(entity.getTimeMillis());
        result.setPosition(entity.getPosition());
        result.setCreatedAt(fromOdt(entity.getCreatedAt()));
        result.setUpdatedAt(fromOdt(entity.getUpdatedAt()));
        return result;
    }

    private static OffsetDateTime toOdt(Date date) {
        return date == null ? null : date.toInstant().atOffset(ZoneOffset.UTC);
    }

    private static Date fromOdt(OffsetDateTime odt) {
        return odt == null ? null : Date.from(odt.toInstant());
    }
}
