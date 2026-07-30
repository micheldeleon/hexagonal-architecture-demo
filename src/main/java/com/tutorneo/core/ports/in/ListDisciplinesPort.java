package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.core.domain.models.Discipline;

public interface ListDisciplinesPort {
    List<Discipline> listAll();
}
