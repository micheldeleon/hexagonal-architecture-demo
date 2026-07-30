package com.tutorneo.adapters.out.persistence.jpa.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.tutorneo.adapters.out.persistence.jpa.entities.DepartmentEntity;

public interface DepartmentRepositoryJpa extends CrudRepository<DepartmentEntity,Long> {

}
