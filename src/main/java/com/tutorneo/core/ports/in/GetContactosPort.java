package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.core.domain.models.ContactoRevelado;

public interface GetContactosPort {
    List<ContactoRevelado> getContactosRecibidos(Long autorId);
    List<ContactoRevelado> getContactosRealizados(Long usuarioId);
}
