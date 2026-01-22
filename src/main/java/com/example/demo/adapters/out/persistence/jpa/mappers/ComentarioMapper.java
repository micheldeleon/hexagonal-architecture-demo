package com.example.demo.adapters.out.persistence.jpa.mappers;

import com.example.demo.adapters.out.persistence.jpa.entities.ComentarioEntity;
import com.example.demo.core.domain.models.Comentario;

public class ComentarioMapper {
    
    public static Comentario toDomain(ComentarioEntity entity) {
        if (entity == null) return null;
        
        return new Comentario(
            entity.getId(),
            entity.getPostId(),
            entity.getAutorId(),
            null, // autorNombre se enriquece después
            null, // autorProfileImageUrl se enriquece después
            entity.getContenido(),
            entity.getComentarioPadreId(),
            entity.getFechaCreacion()
        );
    }
    
    public static ComentarioEntity toEntity(Comentario domain) {
        if (domain == null) return null;
        
        ComentarioEntity entity = new ComentarioEntity();
        entity.setId(domain.getId());
        entity.setPostId(domain.getPostId());
        entity.setAutorId(domain.getAutorId());
        entity.setContenido(domain.getContenido());
        entity.setComentarioPadreId(domain.getComentarioPadreId());
        entity.setFechaCreacion(domain.getFechaCreacion());
        
        return entity;
    }
}
