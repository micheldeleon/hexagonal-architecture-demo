package com.tutorneo.core.ports.out;

import java.util.List;

public interface RepositoryPort<T, ID> {
    void save(T entity);
    T findById(ID id);
    List<T> findAll();
}
