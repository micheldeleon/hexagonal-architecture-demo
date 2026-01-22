package com.example.demo.core.application.usecase;

import com.example.demo.core.domain.models.Comentario;
import com.example.demo.core.domain.models.Post;
import com.example.demo.core.ports.in.CreateComentarioPort;
import com.example.demo.core.ports.out.ComentarioRepositoryPort;
import com.example.demo.core.ports.out.PostRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

public class CreateComentarioUseCase implements CreateComentarioPort {
    
    private final ComentarioRepositoryPort comentarioRepository;
    private final PostRepositoryPort postRepository;
    private final UserRepositoryPort userRepository;
    
    public CreateComentarioUseCase(ComentarioRepositoryPort comentarioRepository, 
                                   PostRepositoryPort postRepository,
                                   UserRepositoryPort userRepository) {
        this.comentarioRepository = comentarioRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public Comentario createComentario(Comentario comentario) {
        // Validaciones
        if (comentario.getContenido() == null || comentario.getContenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido es obligatorio");
        }
        if (comentario.getPostId() == null) {
            throw new IllegalArgumentException("El post es obligatorio");
        }
        if (comentario.getAutorId() == null) {
            throw new IllegalArgumentException("El autor es obligatorio");
        }
        
        // Verificar que el post existe y está activo
        postRepository.findById(comentario.getPostId())
            .orElseThrow(() -> new RuntimeException("El post no existe"));
        
        // Verificar que el usuario existe
        var user = userRepository.findById(comentario.getAutorId());
        if (user == null) {
            throw new RuntimeException("El usuario no existe");
        }
        
        // Si tiene comentario padre, verificar que existe
        if (comentario.getComentarioPadreId() != null) {
            comentarioRepository.findById(comentario.getComentarioPadreId())
                .orElseThrow(() -> new RuntimeException("El comentario padre no existe"));
        }
        
        return comentarioRepository.save(comentario);
    }
}
