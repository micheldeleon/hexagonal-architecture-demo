package com.example.demo.adapters.in.api.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adapters.in.api.dto.ContactoReveladoDto;
import com.example.demo.adapters.in.api.mappers.ContactoReveladoMapperDtos;
import com.example.demo.core.domain.models.ContactoRevelado;
import com.example.demo.core.ports.in.ContactarAvisoPort;
import com.example.demo.core.ports.in.GetContactosPort;

@RestController
@RequestMapping("/api/contactos")
public class ContactoController {
    
    private final ContactarAvisoPort contactarAvisoPort;
    private final GetContactosPort getContactosPort;
    
    public ContactoController(ContactarAvisoPort contactarAvisoPort,
                             GetContactosPort getContactosPort) {
        this.contactarAvisoPort = contactarAvisoPort;
        this.getContactosPort = getContactosPort;
    }
    
    /**
     * Contactar un aviso (revela el teléfono del autor)
     * POST /api/contactos/aviso/{postId}?usuarioId={usuarioId}
     * 
     * Al hacer clic en "Contactar", automáticamente se revela el teléfono 
     * y se envía notificación al autor del aviso
     */
    @PostMapping("/aviso/{postId}")
    public ResponseEntity<ContactoReveladoDto> contactarAviso(
            @PathVariable UUID postId,
            @RequestParam Long usuarioId) {
        try {
            ContactoRevelado contacto = contactarAvisoPort.contactarAviso(postId, usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ContactoReveladoMapperDtos.toDto(contacto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtener contactos recibidos (avisos donde soy autor y me contactaron)
     * GET /api/contactos/recibidos/{autorId}
     */
    @GetMapping("/recibidos/{autorId}")
    public ResponseEntity<List<ContactoReveladoDto>> getContactosRecibidos(@PathVariable Long autorId) {
        List<ContactoRevelado> contactos = getContactosPort.getContactosRecibidos(autorId);
        List<ContactoReveladoDto> dtos = contactos.stream()
            .map(ContactoReveladoMapperDtos::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Obtener contactos realizados (avisos que contacté)
     * GET /api/contactos/realizados/{usuarioId}
     */
    @GetMapping("/realizados/{usuarioId}")
    public ResponseEntity<List<ContactoReveladoDto>> getContactosRealizados(@PathVariable Long usuarioId) {
        List<ContactoRevelado> contactos = getContactosPort.getContactosRealizados(usuarioId);
        List<ContactoReveladoDto> dtos = contactos.stream()
            .map(ContactoReveladoMapperDtos::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
