package com.tutorneo.adapters.out.persistence.jpa.mappers;

import com.tutorneo.adapters.out.persistence.jpa.entities.DepartmentEntity;
import com.tutorneo.core.domain.models.Department;

public class DepartmentMapper {
    public static Department toDomain(DepartmentEntity departmentEntity) {
        if (departmentEntity == null) {
            return null;
        }
        return new Department(
                departmentEntity.getId(),
                departmentEntity.getName());
    }

    public static DepartmentEntity toEntity(Department department) {
        if (department == null) {
            return null;
        }
        return new DepartmentEntity(
                department.getId(),
                department.getName());
    }
}
