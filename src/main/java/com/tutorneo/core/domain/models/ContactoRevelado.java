package com.tutorneo.core.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactoRevelado {
    private UUID id;
    private UUID postId;
    private Long usuarioInteresadoId;
    private Long autorPostId;
    private String telefonoRevelado;
    private LocalDateTime fechaContacto;
    
    // Constructor para creación
    public ContactoRevelado(UUID postId, Long usuarioInteresadoId, Long autorPostId, String telefonoRevelado) {
        this.postId = postId;
        this.usuarioInteresadoId = usuarioInteresadoId;
        this.autorPostId = autorPostId;
        this.telefonoRevelado = telefonoRevelado;
        this.fechaContacto = LocalDateTime.now();
    }
}
