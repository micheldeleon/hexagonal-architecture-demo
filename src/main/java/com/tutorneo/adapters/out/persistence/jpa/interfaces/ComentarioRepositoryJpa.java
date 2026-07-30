package com.tutorneo.adapters.out.persistence.jpa.interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.tutorneo.adapters.out.persistence.jpa.entities.ComentarioEntity;

public interface ComentarioRepositoryJpa extends CrudRepository<ComentarioEntity, UUID> {
    List<ComentarioEntity> findByPostIdOrderByFechaCreacionAsc(UUID postId);
    List<ComentarioEntity> findByAutorId(Long autorId);
}
