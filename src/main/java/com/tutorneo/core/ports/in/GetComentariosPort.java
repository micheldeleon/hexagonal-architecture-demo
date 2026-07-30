package com.tutorneo.core.ports.in;

import java.util.List;
import java.util.UUID;

import com.tutorneo.core.domain.models.Comentario;

public interface GetComentariosPort {
    List<Comentario> getComentariosByPost(UUID postId);
}
