package com.example.demo.adapters.out.persistence.jpa.mappers;

import com.example.demo.adapters.out.persistence.jpa.entities.EstadoPostEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.PostEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.TipoPostEntity;
import com.example.demo.core.domain.models.EstadoPost;
import com.example.demo.core.domain.models.Post;
import com.example.demo.core.domain.models.TipoPost;

public class PostMapper {
    
    public static Post toDomain(PostEntity entity) {
        if (entity == null) return null;
        
        return new Post(
            entity.getId(),
            entity.getTitulo(),
            entity.getContenido(),
            entity.getAutorId(),
            null, // autorNombre se enriquece después
            null, // autorProfileImageUrl se enriquece después
            TipoPost.valueOf(entity.getTipoPost().name()),
            EstadoPost.valueOf(entity.getEstado().name()),
            entity.getDeporte(),
            entity.getUbicacion(),
            entity.getFechaCreacion(),
            entity.getFechaActualizacion()
        );
    }
    
    public static PostEntity toEntity(Post domain) {
        if (domain == null) return null;
        
        PostEntity entity = new PostEntity();
        entity.setId(domain.getId());
        entity.setTitulo(domain.getTitulo());
        entity.setContenido(domain.getContenido());
        entity.setAutorId(domain.getAutorId());
        entity.setTipoPost(TipoPostEntity.valueOf(domain.getTipoPost().name()));
        entity.setEstado(domain.getEstado() != null ? 
            EstadoPostEntity.valueOf(domain.getEstado().name()) : EstadoPostEntity.ACTIVO);
        entity.setDeporte(domain.getDeporte());
        entity.setUbicacion(domain.getUbicacion());
        entity.setFechaCreacion(domain.getFechaCreacion());
        entity.setFechaActualizacion(domain.getFechaActualizacion());
        
        return entity;
    }
}
