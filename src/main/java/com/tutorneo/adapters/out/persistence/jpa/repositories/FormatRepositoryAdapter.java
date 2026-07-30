package com.tutorneo.adapters.out.persistence.jpa.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.tutorneo.adapters.out.persistence.jpa.entities.FormatEntity;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.FormatRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.mappers.FormatMapper;
import com.tutorneo.core.domain.models.Format;
import com.tutorneo.core.ports.out.FormatRepositoryPort;

@Repository
public class FormatRepositoryAdapter implements FormatRepositoryPort {

    private final FormatRepositoryJpa formatRepositoryJpa;

    public FormatRepositoryAdapter(FormatRepositoryJpa formatRepositoryJpa) {
        this.formatRepositoryJpa = formatRepositoryJpa;
    }

    @Override
    public void save(Format entity) {
        formatRepositoryJpa.save(FormatMapper.toEntity(entity));
    }

    @Override
    public Format findById(Long id) {
        Optional<FormatEntity> entity = formatRepositoryJpa.findById(id);
        return entity.map(FormatMapper::toDomain).orElse(null);
    }

    @Override
    public List<Format> findAll() {
        Iterable<FormatEntity> entities = formatRepositoryJpa.findAll();
        List<Format> formats = new ArrayList<>();
        for (FormatEntity entity : entities) {
            formats.add(FormatMapper.toDomain(entity));
        }
        return formats;
    }

    @Override
    public List<Format> findByDisciplineId(Long disciplineId) {
        List<Long> ids = formatRepositoryJpa.findIdsByDisciplineId(disciplineId);
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        Iterable<FormatEntity> entities = formatRepositoryJpa.findAllById(ids);
        List<Format> formats = new ArrayList<>();
        for (FormatEntity entity : entities) {
            formats.add(FormatMapper.toDomain(entity));
        }
        return formats;
    }
}
