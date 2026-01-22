package com.example.demo.core.application.usecase;

import java.util.List;

import com.example.demo.core.domain.models.ContactoRevelado;
import com.example.demo.core.ports.in.GetContactosPort;
import com.example.demo.core.ports.out.ContactoReveladoRepositoryPort;

public class GetContactosUseCase implements GetContactosPort {
    
    private final ContactoReveladoRepositoryPort contactoRepository;
    
    public GetContactosUseCase(ContactoReveladoRepositoryPort contactoRepository) {
        this.contactoRepository = contactoRepository;
    }
    
    @Override
    public List<ContactoRevelado> getContactosRecibidos(Long autorId) {
        return contactoRepository.findByAutorPostId(autorId);
    }
    
    @Override
    public List<ContactoRevelado> getContactosRealizados(Long usuarioId) {
        return contactoRepository.findByUsuarioInteresadoId(usuarioId);
    }
}
