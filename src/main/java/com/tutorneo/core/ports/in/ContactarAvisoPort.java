package com.tutorneo.core.ports.in;

import java.util.UUID;

import com.tutorneo.core.domain.models.ContactoRevelado;

public interface ContactarAvisoPort {
    ContactoRevelado contactarAviso(UUID postId, Long usuarioInteresadoId);
}
