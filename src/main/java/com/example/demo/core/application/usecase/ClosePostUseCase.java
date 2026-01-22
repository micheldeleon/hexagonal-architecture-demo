package com.example.demo.core.application.usecase;

import java.util.UUID;

import com.example.demo.core.domain.models.Post;
import com.example.demo.core.ports.in.ClosePostPort;
import com.example.demo.core.ports.out.PostRepositoryPort;

public class ClosePostUseCase implements ClosePostPort {
    
    private final PostRepositoryPort postRepository;
    
    public ClosePostUseCase(PostRepositoryPort postRepository) {
        this.postRepository = postRepository;
    }
    
    @Override
    public void closePost(UUID postId, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post no encontrado"));
        
        // Solo el autor puede cerrar el post
        if (!post.getAutorId().equals(userId)) {
            throw new RuntimeException("Solo el autor puede cerrar el post");
        }
        
        post.cerrar();
        postRepository.save(post);
    }
}
