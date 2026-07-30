package com.tutorneo.core.application.usecase;

import java.util.UUID;

import com.tutorneo.core.domain.models.ContactoRevelado;
import com.tutorneo.core.domain.models.Post;
import com.tutorneo.core.domain.models.User;
import com.tutorneo.core.ports.in.ContactarAvisoPort;
import com.tutorneo.core.ports.out.ContactoReveladoRepositoryPort;
import com.tutorneo.core.ports.out.PostRepositoryPort;
import com.tutorneo.core.ports.out.UserRepositoryPort;

public class ContactarAvisoUseCase implements ContactarAvisoPort {
    
    private final ContactoReveladoRepositoryPort contactoRepository;
    private final PostRepositoryPort postRepository;
    private final UserRepositoryPort userRepository;
    
    public ContactarAvisoUseCase(ContactoReveladoRepositoryPort contactoRepository,
                                  PostRepositoryPort postRepository,
                                  UserRepositoryPort userRepository) {
        this.contactoRepository = contactoRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public ContactoRevelado contactarAviso(UUID postId, Long usuarioInteresadoId) {
        // Verificar que el post existe y es un aviso
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("El post no existe"));
        
        if (!post.isAviso()) {
            throw new RuntimeException("Solo se puede contactar en avisos");
        }
        
        // No permitir que el autor contacte su propio aviso
        if (post.getAutorId().equals(usuarioInteresadoId)) {
            throw new RuntimeException("No puedes contactar tu propio aviso");
        }
        
        // Verificar que no haya contactado antes
        if (contactoRepository.existsByPostIdAndUsuarioInteresadoId(postId, usuarioInteresadoId)) {
            throw new RuntimeException("Ya has contactado este aviso");
        }
        
        // Obtener el teléfono del autor del post
        User autor = userRepository.findById(post.getAutorId());
        if (autor == null) {
            throw new RuntimeException("El autor del post no existe");
        }
        
        String telefono = autor.getPhoneNumber();
        if (telefono == null || telefono.trim().isEmpty()) {
            telefono = "No disponible";
        }
        
        // Crear el contacto revelado
        ContactoRevelado contacto = new ContactoRevelado(
            postId,
            usuarioInteresadoId,
            post.getAutorId(),
            telefono
        );
        
        return contactoRepository.save(contacto);
    }
}
