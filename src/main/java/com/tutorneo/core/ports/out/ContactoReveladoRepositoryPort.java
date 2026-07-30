package com.tutorneo.core.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.tutorneo.core.domain.models.ContactoRevelado;

public interface ContactoReveladoRepositoryPort {
    ContactoRevelado save(ContactoRevelado contacto);
    Optional<ContactoRevelado> findById(UUID id);
    List<ContactoRevelado> findByPostId(UUID postId);
    List<ContactoRevelado> findByUsuarioInteresadoId(Long usuarioId);
    List<ContactoRevelado> findByAutorPostId(Long autorId);
    boolean existsByPostIdAndUsuarioInteresadoId(UUID postId, Long usuarioId);
}
