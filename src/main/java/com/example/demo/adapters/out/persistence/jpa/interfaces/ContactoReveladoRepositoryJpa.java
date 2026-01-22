package com.example.demo.adapters.out.persistence.jpa.interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.adapters.out.persistence.jpa.entities.ContactoReveladoEntity;

public interface ContactoReveladoRepositoryJpa extends CrudRepository<ContactoReveladoEntity, UUID> {
    List<ContactoReveladoEntity> findByPostId(UUID postId);
    List<ContactoReveladoEntity> findByUsuarioInteresadoId(Long usuarioId);
    List<ContactoReveladoEntity> findByAutorPostId(Long autorId);
    boolean existsByPostIdAndUsuarioInteresadoId(UUID postId, Long usuarioId);
}
