package com.tutorneo.core.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.tutorneo.core.domain.models.Post;
import com.tutorneo.core.domain.models.TipoPost;

public interface PostRepositoryPort {
    Post save(Post post);
    Optional<Post> findById(UUID id);
    List<Post> findAll();
    List<Post> findByTipoPost(TipoPost tipoPost);
    List<Post> findByAutorId(Long autorId);
    void deleteById(UUID id);
}
