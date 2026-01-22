package com.example.demo.core.application.usecase;

import java.util.List;
import java.util.UUID;

import com.example.demo.core.domain.models.Comentario;
import com.example.demo.core.ports.in.GetComentariosPort;
import com.example.demo.core.ports.out.ComentarioRepositoryPort;

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
