package com.example.demo.core.ports.in;

import java.util.UUID;

import com.example.demo.core.domain.models.ContactoRevelado;

public interface ContactarAvisoPort {
    ContactoRevelado contactarAviso(UUID postId, Long usuarioInteresadoId);
}
