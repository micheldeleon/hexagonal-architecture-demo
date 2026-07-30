package com.tutorneo.core.application.usecase;

import java.util.List;
import java.util.UUID;

import com.tutorneo.core.domain.models.Comentario;
import com.tutorneo.core.ports.in.GetComentariosPort;
import com.tutorneo.core.ports.out.ComentarioRepositoryPort;

public class GetComentariosUseCase implements GetComentariosPort {
    
    private final ComentarioRepositoryPort comentarioRepository;
    
    public GetComentariosUseCase(ComentarioRepositoryPort comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }
    
    @Override
    public List<Comentario> getComentariosByPost(UUID postId) {
        return comentarioRepository.findByPostId(postId);
    }
}
