package com.example.demo.core.application.usecase;

import com.example.demo.core.domain.models.Post;
import com.example.demo.core.ports.in.CreatePostPort;
import com.example.demo.core.ports.out.PostRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

public class CreatePostUseCase implements CreatePostPort {
    
    private final PostRepositoryPort postRepository;
    private final UserRepositoryPort userRepository;
    
    public CreatePostUseCase(PostRepositoryPort postRepository, UserRepositoryPort userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public Post createPost(Post post) {
        // Validaciones
        if (post.getTitulo() == null || post.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (post.getContenido() == null || post.getContenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido es obligatorio");
        }
        if (post.getAutorId() == null) {
            throw new IllegalArgumentException("El autor es obligatorio");
        }
        if (post.getTipoPost() == null) {
            throw new IllegalArgumentException("El tipo de post es obligatorio");
        }
        
        // Verificar que el usuario existe
        var user = userRepository.findById(post.getAutorId());
        if (user == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        
        return postRepository.save(post);
    }
}
