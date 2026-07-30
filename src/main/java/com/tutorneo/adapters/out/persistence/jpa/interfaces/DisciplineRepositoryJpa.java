package com.tutorneo.adapters.out.persistence.jpa.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.tutorneo.adapters.out.persistence.jpa.entities.DisciplineEntity;

public interface DisciplineRepositoryJpa extends CrudRepository<DisciplineEntity, Long> {
}
