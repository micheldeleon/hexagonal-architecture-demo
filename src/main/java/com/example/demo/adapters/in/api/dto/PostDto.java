package com.example.demo.adapters.in.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private UUID id;
    private String titulo;
    private String contenido;
    private Long autorId;
    private String autorNombre;
    private String autorProfileImageUrl;
    private String tipoPost;
    private String estado;
    private String deporte;
    private String ubicacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
