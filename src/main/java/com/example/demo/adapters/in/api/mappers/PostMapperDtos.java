package com.example.demo.adapters.in.api.mappers;

import com.example.demo.adapters.in.api.dto.CreatePostDto;
import com.example.demo.adapters.in.api.dto.PostDto;
import com.example.demo.core.domain.models.Post;
import com.example.demo.core.domain.models.TipoPost;

public class PostMapperDtos {
    
    public static Post toDomain(CreatePostDto dto) {
        return new Post(
            dto.getTitulo(),
            dto.getContenido(),
            dto.getAutorId(),
            TipoPost.valueOf(dto.getTipoPost()),
            dto.getDeporte(),
            dto.getUbicacion()
        );
    }
    
    public static PostDto toDto(Post domain) {
        return new PostDto(
            domain.getId(),
            domain.getTitulo(),
            domain.getContenido(),
            domain.getAutorId(),
            domain.getAutorNombre(),
            domain.getAutorProfileImageUrl(),
            domain.getTipoPost().name(),
            domain.getEstado().name(),
            domain.getDeporte(),
            domain.getUbicacion(),
            domain.getFechaCreacion(),
            domain.getFechaActualizacion()
        );
    }
}
