package com.tutorneo.adapters.out.persistence.jpa.interfaces;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.tutorneo.adapters.out.persistence.jpa.entities.RoleEntity;

public interface RoleRepositoryJpa extends CrudRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
}
