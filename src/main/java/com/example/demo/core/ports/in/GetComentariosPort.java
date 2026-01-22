package com.example.demo.core.ports.in;

import java.util.List;
import java.util.UUID;

import com.example.demo.core.domain.models.Comentario;

public interface GetComentariosPort {
    List<Comentario> getComentariosByPost(UUID postId);
}
