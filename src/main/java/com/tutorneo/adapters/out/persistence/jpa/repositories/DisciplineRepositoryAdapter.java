package com.tutorneo.adapters.out.persistence.jpa.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.tutorneo.adapters.out.persistence.jpa.entities.DisciplineEntity;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.DisciplineRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.mappers.DisciplineMapper;
import com.tutorneo.core.domain.models.Discipline;
import com.tutorneo.core.ports.out.DisciplineRepositoryPort;

@Repository
public class DisciplineRepositoryAdapter implements DisciplineRepositoryPort {

    private final DisciplineRepositoryJpa disciplineRepositoryJpa;

    public DisciplineRepositoryAdapter(DisciplineRepositoryJpa disciplineRepositoryJpa) {
        this.disciplineRepositoryJpa = disciplineRepositoryJpa;
    }

    @Override
    public void save(Discipline entity) {
        disciplineRepositoryJpa.save(DisciplineMapper.toEntity(entity));
    }

    @Override
    public Discipline findById(Long id) {
        Optional<DisciplineEntity> entity = disciplineRepositoryJpa.findById(id);
        return entity.map(DisciplineMapper::toDomain).orElse(null);
    }

    @Override
    public List<Discipline> findAll() {
        Iterable<DisciplineEntity> entities = disciplineRepositoryJpa.findAll();
        List<Discipline> disciplines = new ArrayList<>();
        for (DisciplineEntity entity : entities) {
            disciplines.add(DisciplineMapper.toDomain(entity));
        }
        return disciplines;
    }
}
