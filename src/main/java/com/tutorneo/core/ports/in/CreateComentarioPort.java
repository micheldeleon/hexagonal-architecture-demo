package com.tutorneo.core.ports.in;

import com.tutorneo.core.domain.models.Comentario;

public interface CreateComentarioPort {
    Comentario createComentario(Comentario comentario);
}
