package com.example.demo.adapters.out.persistence.jpa.mappers;

import com.example.demo.adapters.out.persistence.jpa.entities.ContactoReveladoEntity;
import com.example.demo.core.domain.models.ContactoRevelado;

public class ContactoReveladoMapper {
    
    public static ContactoRevelado toDomain(ContactoReveladoEntity entity) {
        if (entity == null) return null;
        
        return new ContactoRevelado(
            entity.getId(),
            entity.getPostId(),
            entity.getUsuarioInteresadoId(),
            entity.getAutorPostId(),
            entity.getTelefonoRevelado(),
            entity.getFechaContacto()
        );
    }
    
    public static ContactoReveladoEntity toEntity(ContactoRevelado domain) {
        if (domain == null) return null;
        
        ContactoReveladoEntity entity = new ContactoReveladoEntity();
        entity.setId(domain.getId());
        entity.setPostId(domain.getPostId());
        entity.setUsuarioInteresadoId(domain.getUsuarioInteresadoId());
        entity.setAutorPostId(domain.getAutorPostId());
        entity.setTelefonoRevelado(domain.getTelefonoRevelado());
        entity.setFechaContacto(domain.getFechaContacto());
        
        return entity;
    }
}
