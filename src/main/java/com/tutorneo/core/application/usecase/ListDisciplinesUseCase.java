package com.tutorneo.core.application.usecase;

import java.util.List;

import com.tutorneo.core.domain.models.Discipline;
import com.tutorneo.core.ports.in.ListDisciplinesPort;
import com.tutorneo.core.ports.out.DisciplineRepositoryPort;


public class ListDisciplinesUseCase implements ListDisciplinesPort {

    private final DisciplineRepositoryPort disciplineRepositoryPort;

    public ListDisciplinesUseCase(DisciplineRepositoryPort disciplineRepositoryPort) {
        this.disciplineRepositoryPort = disciplineRepositoryPort;
    }

    @Override
    public List<Discipline> listAll() {
        return disciplineRepositoryPort.findAll();
    }
}
