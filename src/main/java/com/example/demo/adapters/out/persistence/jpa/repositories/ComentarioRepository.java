package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.demo.adapters.out.persistence.jpa.entities.ComentarioEntity;
import com.example.demo.adapters.out.persistence.jpa.interfaces.ComentarioRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.UserRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.mappers.ComentarioMapper;
import com.example.demo.core.domain.models.Comentario;
import com.example.demo.core.ports.out.ComentarioRepositoryPort;

@Repository
public class ComentarioRepository implements ComentarioRepositoryPort {
    
    private final ComentarioRepositoryJpa comentarioRepositoryJpa;
    private final UserRepositoryJpa userRepositoryJpa;
    
    public ComentarioRepository(ComentarioRepositoryJpa comentarioRepositoryJpa, UserRepositoryJpa userRepositoryJpa) {
        this.comentarioRepositoryJpa = comentarioRepositoryJpa;
        this.userRepositoryJpa = userRepositoryJpa;
    }
    
    @Override
    public Comentario save(Comentario comentario) {
        ComentarioEntity entity = ComentarioMapper.toEntity(comentario);
        ComentarioEntity saved = comentarioRepositoryJpa.save(entity);
        
        Comentario result = ComentarioMapper.toDomain(saved);
        enrichWithAutorNombre(result);
        return result;
    }
    
    @Override
    public Optional<Comentario> findById(UUID id) {
        return comentarioRepositoryJpa.findById(id)
            .map(entity -> {
                Comentario comentario = ComentarioMapper.toDomain(entity);
                enrichWithAutorNombre(comentario);
                return comentario;
            });
    }
    
    @Override
    public List<Comentario> findByPostId(UUID postId) {
        return comentarioRepositoryJpa.findByPostIdOrderByFechaCreacionAsc(postId).stream()
            .map(entity -> {
                Comentario comentario = ComentarioMapper.toDomain(entity);
                enrichWithAutorNombre(comentario);
                return comentario;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Comentario> findByAutorId(Long autorId) {
        return comentarioRepositoryJpa.findByAutorId(autorId).stream()
            .map(entity -> {
                Comentario comentario = ComentarioMapper.toDomain(entity);
                enrichWithAutorNombre(comentario);
                return comentario;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UUID id) {
        comentarioRepositoryJpa.deleteById(id);
    }
    
    private void enrichWithAutorNombre(Comentario comentario) {
        if (comentario.getAutorId() != null) {
            userRepositoryJpa.findById(comentario.getAutorId()).ifPresent(user -> {
                comentario.setAutorNombre(user.getName() + " " + user.getLastName());
                comentario.setAutorProfileImageUrl(user.getProfileImageUrl());
            });
        }
    }
}
