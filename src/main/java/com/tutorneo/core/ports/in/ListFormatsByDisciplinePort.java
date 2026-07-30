package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.core.domain.models.Format;

public interface ListFormatsByDisciplinePort {
    List<Format> listByDisciplineId(Long disciplineId);

}
