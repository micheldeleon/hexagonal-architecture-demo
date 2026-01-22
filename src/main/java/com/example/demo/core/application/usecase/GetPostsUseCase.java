package com.example.demo.core.application.usecase;

import java.util.List;
import java.util.UUID;

import com.example.demo.core.domain.models.Post;
import com.example.demo.core.domain.models.TipoPost;
import com.example.demo.core.ports.in.GetPostsPort;
import com.example.demo.core.ports.out.PostRepositoryPort;

public class GetPostsUseCase implements GetPostsPort {
    
    private final PostRepositoryPort postRepository;
    
    public GetPostsUseCase(PostRepositoryPort postRepository) {
        this.postRepository = postRepository;
    }
    
    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    @Override
    public Post getPostById(UUID id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post no encontrado con id: " + id));
    }
    
    @Override
    public List<Post> getPostsByTipo(TipoPost tipo) {
        return postRepository.findByTipoPost(tipo);
    }
    
    @Override
    public List<Post> getPostsByAutor(Long autorId) {
        return postRepository.findByAutorId(autorId);
    }
}
