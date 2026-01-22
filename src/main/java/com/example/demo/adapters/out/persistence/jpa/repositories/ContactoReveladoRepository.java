package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.demo.adapters.out.persistence.jpa.entities.ContactoReveladoEntity;
import com.example.demo.adapters.out.persistence.jpa.interfaces.ContactoReveladoRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.mappers.ContactoReveladoMapper;
import com.example.demo.core.domain.models.ContactoRevelado;
import com.example.demo.core.ports.out.ContactoReveladoRepositoryPort;

@Repository
public class ContactoReveladoRepository implements ContactoReveladoRepositoryPort {
    
    private final ContactoReveladoRepositoryJpa contactoRepositoryJpa;
    
    public ContactoReveladoRepository(ContactoReveladoRepositoryJpa contactoRepositoryJpa) {
        this.contactoRepositoryJpa = contactoRepositoryJpa;
    }
    
    @Override
    public ContactoRevelado save(ContactoRevelado contacto) {
        ContactoReveladoEntity entity = ContactoReveladoMapper.toEntity(contacto);
        ContactoReveladoEntity saved = contactoRepositoryJpa.save(entity);
        return ContactoReveladoMapper.toDomain(saved);
    }
    
    @Override
    public Optional<ContactoRevelado> findById(UUID id) {
        return contactoRepositoryJpa.findById(id)
            .map(ContactoReveladoMapper::toDomain);
    }
    
    @Override
    public List<ContactoRevelado> findByPostId(UUID postId) {
        return contactoRepositoryJpa.findByPostId(postId).stream()
            .map(ContactoReveladoMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ContactoRevelado> findByUsuarioInteresadoId(Long usuarioId) {
        return contactoRepositoryJpa.findByUsuarioInteresadoId(usuarioId).stream()
            .map(ContactoReveladoMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ContactoRevelado> findByAutorPostId(Long autorId) {
        return contactoRepositoryJpa.findByAutorPostId(autorId).stream()
            .map(ContactoReveladoMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByPostIdAndUsuarioInteresadoId(UUID postId, Long usuarioId) {
        return contactoRepositoryJpa.existsByPostIdAndUsuarioInteresadoId(postId, usuarioId);
    }
}
