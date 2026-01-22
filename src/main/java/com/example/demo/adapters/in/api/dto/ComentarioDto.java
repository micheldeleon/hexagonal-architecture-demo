package com.example.demo.adapters.in.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioDto {
    private UUID id;
    private UUID postId;
    private Long autorId;
    private String autorNombre;
    private String autorProfileImageUrl;
    private String contenido;
    private UUID comentarioPadreId;
    private LocalDateTime fechaCreacion;
}
