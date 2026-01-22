package com.example.demo.adapters.in.api.mappers;

import com.example.demo.adapters.in.api.dto.ContactoReveladoDto;
import com.example.demo.core.domain.models.ContactoRevelado;

public class ContactoReveladoMapperDtos {
    
    public static ContactoReveladoDto toDto(ContactoRevelado domain) {
        return new ContactoReveladoDto(
            domain.getId(),
            domain.getPostId(),
            domain.getUsuarioInteresadoId(),
            domain.getAutorPostId(),
            domain.getTelefonoRevelado(),
            domain.getFechaContacto()
        );
    }
}
