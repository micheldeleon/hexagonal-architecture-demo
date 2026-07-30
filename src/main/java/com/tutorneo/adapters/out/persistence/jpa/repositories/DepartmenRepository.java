package com.tutorneo.adapters.out.persistence.jpa.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tutorneo.adapters.out.persistence.jpa.interfaces.DepartmentRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.mappers.DepartmentMapper;
import com.tutorneo.core.domain.models.Department;
import com.tutorneo.core.ports.out.DepartmentRepositoryPort;

import jakarta.transaction.Transactional;

@Repository
public class DepartmenRepository implements DepartmentRepositoryPort {

    private final DepartmentRepositoryJpa repo;

    public DepartmenRepository(DepartmentRepositoryJpa departmentRepositoryJpa) {
        this.repo = departmentRepositoryJpa;
    }

    @Override
    @Transactional
    public void save(Department entity) {
        repo.save(DepartmentMapper.toEntity(entity));
    }

    @Override
    public Department findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public List<Department> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
