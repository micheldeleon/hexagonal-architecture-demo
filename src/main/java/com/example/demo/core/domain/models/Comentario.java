package com.example.demo.core.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comentario {
    private UUID id;
    private UUID postId;
    private Long autorId;
    private String autorNombre; // Denormalizado
    private String autorProfileImageUrl; // Foto de perfil del autor
    private String contenido;
    private UUID comentarioPadreId;
    private LocalDateTime fechaCreacion;
    
    // Constructor para creación
    public Comentario(UUID postId, Long autorId, String contenido, UUID comentarioPadreId) {
        this.postId = postId;
        this.autorId = autorId;
        this.contenido = contenido;
        this.comentarioPadreId = comentarioPadreId;
        this.fechaCreacion = LocalDateTime.now();
    }
}
