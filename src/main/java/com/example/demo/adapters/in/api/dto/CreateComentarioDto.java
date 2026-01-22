package com.example.demo.adapters.in.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateComentarioDto {
    
    @NotNull(message = "El post es obligatorio")
    private UUID postId;
    
    @NotNull(message = "El autor es obligatorio")
    private Long autorId;
    
    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;
    
    private UUID comentarioPadreId; // Opcional, para respuestas
}
