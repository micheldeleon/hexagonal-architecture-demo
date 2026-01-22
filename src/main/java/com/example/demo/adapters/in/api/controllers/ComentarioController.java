package com.example.demo.adapters.in.api.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adapters.in.api.dto.ComentarioDto;
import com.example.demo.adapters.in.api.dto.CreateComentarioDto;
import com.example.demo.adapters.in.api.mappers.ComentarioMapperDtos;
import com.example.demo.core.domain.models.Comentario;
import com.example.demo.core.ports.in.CreateComentarioPort;
import com.example.demo.core.ports.in.GetComentariosPort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comentarios")
@Validated
public class ComentarioController {
    
    private final CreateComentarioPort createComentarioPort;
    private final GetComentariosPort getComentariosPort;
    
    public ComentarioController(CreateComentarioPort createComentarioPort,
                                GetComentariosPort getComentariosPort) {
        this.createComentarioPort = createComentarioPort;
        this.getComentariosPort = getComentariosPort;
    }
    
    /**
     * Crear un nuevo comentario
     * POST /api/comentarios
     */
    @PostMapping
    public ResponseEntity<ComentarioDto> createComentario(@Valid @RequestBody CreateComentarioDto dto) {
        try {
            Comentario comentario = ComentarioMapperDtos.toDomain(dto);
            Comentario created = createComentarioPort.createComentario(comentario);
            return ResponseEntity.status(HttpStatus.CREATED).body(ComentarioMapperDtos.toDto(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener comentarios de un post
     * GET /api/comentarios/post/{postId}
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ComentarioDto>> getComentariosByPost(@PathVariable UUID postId) {
        List<Comentario> comentarios = getComentariosPort.getComentariosByPost(postId);
        List<ComentarioDto> dtos = comentarios.stream()
            .map(ComentarioMapperDtos::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
