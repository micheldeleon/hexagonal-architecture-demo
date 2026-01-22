package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.demo.adapters.out.persistence.jpa.entities.PostEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.TipoPostEntity;
import com.example.demo.adapters.out.persistence.jpa.interfaces.PostRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.UserRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.mappers.PostMapper;
import com.example.demo.core.domain.models.Post;
import com.example.demo.core.domain.models.TipoPost;
import com.example.demo.core.ports.out.PostRepositoryPort;

@Repository
public class PostRepository implements PostRepositoryPort {
    
    private final PostRepositoryJpa postRepositoryJpa;
    private final UserRepositoryJpa userRepositoryJpa;
    
    public PostRepository(PostRepositoryJpa postRepositoryJpa, UserRepositoryJpa userRepositoryJpa) {
        this.postRepositoryJpa = postRepositoryJpa;
        this.userRepositoryJpa = userRepositoryJpa;
    }
    
    @Override
    public Post save(Post post) {
        PostEntity entity = PostMapper.toEntity(post);
        PostEntity saved = postRepositoryJpa.save(entity);
        
        // Enriquecer con nombre del autor
        Post result = PostMapper.toDomain(saved);
        enrichWithAutorNombre(result);
        return result;
    }
    
    @Override
    public Optional<Post> findById(UUID id) {
        return postRepositoryJpa.findById(id)
            .map(entity -> {
                Post post = PostMapper.toDomain(entity);
                enrichWithAutorNombre(post);
                return post;
            });
    }
    
    @Override
    public List<Post> findAll() {
        List<PostEntity> entities = postRepositoryJpa.findAllByOrderByFechaCreacionDesc();
        return entities.stream()
            .map(entity -> {
                Post post = PostMapper.toDomain(entity);
                enrichWithAutorNombre(post);
                return post;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Post> findByTipoPost(TipoPost tipoPost) {
        TipoPostEntity tipoEntity = TipoPostEntity.valueOf(tipoPost.name());
        return postRepositoryJpa.findByTipoPost(tipoEntity).stream()
            .map(entity -> {
                Post post = PostMapper.toDomain(entity);
                enrichWithAutorNombre(post);
                return post;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Post> findByAutorId(Long autorId) {
        return postRepositoryJpa.findByAutorId(autorId).stream()
            .map(entity -> {
                Post post = PostMapper.toDomain(entity);
                enrichWithAutorNombre(post);
                return post;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UUID id) {
        postRepositoryJpa.deleteById(id);
    }
    
    private void enrichWithAutorNombre(Post post) {
        if (post.getAutorId() != null) {
            userRepositoryJpa.findById(post.getAutorId()).ifPresent(user -> {
                post.setAutorNombre(user.getName() + " " + user.getLastName());
                post.setAutorProfileImageUrl(user.getProfileImageUrl());
            });
        }
    }
}
