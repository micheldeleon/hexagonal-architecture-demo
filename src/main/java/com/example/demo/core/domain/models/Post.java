package com.example.demo.core.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private UUID id;
    private String titulo;
    private String contenido;
    private Long autorId;
    private String autorNombre; // Denormalizado para facilitar consultas
    private String autorProfileImageUrl; // Foto de perfil del autor
    private TipoPost tipoPost;
    private EstadoPost estado;
    private String deporte;
    private String ubicacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    // Constructor para creación
    public Post(String titulo, String contenido, Long autorId, TipoPost tipoPost, String deporte, String ubicacion) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.autorId = autorId;
        this.tipoPost = tipoPost;
        this.estado = EstadoPost.ACTIVO;
        this.deporte = deporte;
        this.ubicacion = ubicacion;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    public boolean isAviso() {
        return tipoPost == TipoPost.BUSCO_EQUIPO || 
               tipoPost == TipoPost.EQUIPO_BUSCA_JUGADOR || 
               tipoPost == TipoPost.PARTIDO_URGENTE;
    }
    
    public void cerrar() {
        this.estado = EstadoPost.CERRADO;
        this.fechaActualizacion = LocalDateTime.now();
    }
}
