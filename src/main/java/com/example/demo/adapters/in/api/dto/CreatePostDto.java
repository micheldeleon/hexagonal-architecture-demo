package com.example.demo.adapters.in.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede tener más de 200 caracteres")
    private String titulo;
    
    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;
    
    @NotNull(message = "El autor es obligatorio")
    private Long autorId;
    
    @NotBlank(message = "El tipo de post es obligatorio")
    private String tipoPost; // CHAT_GENERAL, NOTICIA, BUSCO_EQUIPO, EQUIPO_BUSCA_JUGADOR, PARTIDO_URGENTE
    
    private String deporte;
    
    private String ubicacion;
}
