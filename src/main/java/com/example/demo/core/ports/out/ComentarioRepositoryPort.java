package com.example.demo.core.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.core.domain.models.Comentario;

public interface ComentarioRepositoryPort {
    Comentario save(Comentario comentario);
    Optional<Comentario> findById(UUID id);
    List<Comentario> findByPostId(UUID postId);
    List<Comentario> findByAutorId(Long autorId);
    void deleteById(UUID id);
}
