package com.tutorneo.adapters.out.persistence.jpa.mappers;

import com.tutorneo.adapters.out.persistence.jpa.entities.EliminationFormatEntity;
import com.tutorneo.adapters.out.persistence.jpa.entities.FormatEntity;
import com.tutorneo.adapters.out.persistence.jpa.entities.LeagueFormatEntity;
import com.tutorneo.adapters.out.persistence.jpa.entities.RaceFormatEntity;
import com.tutorneo.core.domain.models.Format;
import com.tutorneo.core.domain.models.SimpleFormat;
import com.tutorneo.core.domain.models.Formats.EliminationFormat;
import com.tutorneo.core.domain.models.Formats.LeagueFormat;
import com.tutorneo.core.domain.models.Formats.RaceFormat;

public class FormatMapper {

    private FormatMapper() {
    }

    public static Format toDomain(FormatEntity entity) {
        if (entity == null) {
            return null;
        }

        Format format;
        if (entity instanceof LeagueFormatEntity) {
            LeagueFormatEntity leagueEntity = (LeagueFormatEntity) entity;
            LeagueFormat league = new LeagueFormat();
            league.setWinPoints(leagueEntity.getWinPoints());
            league.setDrawPoints(leagueEntity.getDrawPoints());
            league.setLossPoints(leagueEntity.getLossPoints());
            league.setHasTiebreak(leagueEntity.isHasTiebreak());
            format = league;
        } else if (entity instanceof RaceFormatEntity) {
            format = new RaceFormat();
        } else if (entity instanceof EliminationFormatEntity) {
            format = new EliminationFormat();
        } else {
            format = new SimpleFormat();
        }

        format.setId(entity.getId());
        format.setName(entity.getName());
        format.setGeneraFixture(entity.isGeneraFixture());
        return format;
    }

    public static FormatEntity toEntity(Format format) {
        if (format == null) {
            return null;
        }

        // Si solo viene el id (p.ej. como referencia en Tournament), devolvemos un stub para evitar insertar datos incompletos.
        if (format.getId() != null && format.getName() == null) {
            FormatEntity ref = new FormatEntity();
            ref.setId(format.getId());
            return ref;
        }

        FormatEntity entity;
        if (format instanceof LeagueFormat) {
            LeagueFormat league = (LeagueFormat) format;
            LeagueFormatEntity leagueEntity = new LeagueFormatEntity();
            leagueEntity.setWinPoints(league.getWinPoints());
            leagueEntity.setDrawPoints(league.getDrawPoints());
            leagueEntity.setLossPoints(league.getLossPoints());
            leagueEntity.setHasTiebreak(league.isHasTiebreak());
            entity = leagueEntity;
        } else if (format instanceof RaceFormat) {
            entity = new RaceFormatEntity();
        } else if (format instanceof EliminationFormat) {
            entity = new EliminationFormatEntity();
        } else {
            entity = new FormatEntity();
        }

        entity.setId(format.getId());
        entity.setName(format.getName());
        entity.setGeneraFixture(format.isGeneraFixture());
        return entity;
    }
}
