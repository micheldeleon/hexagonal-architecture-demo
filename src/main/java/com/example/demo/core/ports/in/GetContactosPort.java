package com.example.demo.core.ports.in;

import java.util.List;

import com.example.demo.core.domain.models.ContactoRevelado;

public interface GetContactosPort {
    List<ContactoRevelado> getContactosRecibidos(Long autorId);
    List<ContactoRevelado> getContactosRealizados(Long usuarioId);
}
