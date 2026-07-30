package com.tutorneo.core.ports.out;

import com.tutorneo.core.domain.models.Format;
import java.util.List;

public interface FormatRepositoryPort extends RepositoryPort<Format, Long>{
    List<Format> findByDisciplineId(Long disciplineId);
}
