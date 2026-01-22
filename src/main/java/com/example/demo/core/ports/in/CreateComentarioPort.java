package com.example.demo.core.ports.in;

import com.example.demo.core.domain.models.Comentario;

public interface CreateComentarioPort {
    Comentario createComentario(Comentario comentario);
}
