package com.tutorneo.adapters.in.api.mappers;

import com.tutorneo.adapters.in.api.dto.ComentarioDto;
import com.tutorneo.adapters.in.api.dto.CreateComentarioDto;
import com.tutorneo.core.domain.models.Comentario;

public class ComentarioMapperDtos {
    
    public static Comentario toDomain(CreateComentarioDto dto) {
        return new Comentario(
            dto.getPostId(),
            dto.getAutorId(),
            dto.getContenido(),
            dto.getComentarioPadreId()
        );
    }
    
    public static ComentarioDto toDto(Comentario domain) {
        return new ComentarioDto(
            domain.getId(),
            domain.getPostId(),
            domain.getAutorId(),
            domain.getAutorNombre(),
            domain.getAutorProfileImageUrl(),
            domain.getContenido(),
            domain.getComentarioPadreId(),
            domain.getFechaCreacion()
        );
    }
}
