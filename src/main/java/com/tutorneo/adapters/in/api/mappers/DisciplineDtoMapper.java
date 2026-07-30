package com.tutorneo.adapters.in.api.mappers;

import com.tutorneo.adapters.in.api.dto.DisciplineResponse;
import com.tutorneo.core.domain.models.Discipline;

public class DisciplineDtoMapper {

    private DisciplineDtoMapper() {
    }

    public static DisciplineResponse toResponse(Discipline discipline) {
        if (discipline == null) {
            return null;
        }
        return new DisciplineResponse(discipline.getId(), discipline.getName(), discipline.isCollective());
    }
}
