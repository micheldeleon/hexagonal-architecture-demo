package com.example.demo.adapters.out.persistence.jpa.interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.adapters.out.persistence.jpa.entities.PostEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.TipoPostEntity;

public interface PostRepositoryJpa extends CrudRepository<PostEntity, UUID> {
    List<PostEntity> findByTipoPost(TipoPostEntity tipoPost);
    List<PostEntity> findByAutorId(Long autorId);
    List<PostEntity> findAllByOrderByFechaCreacionDesc();
}
