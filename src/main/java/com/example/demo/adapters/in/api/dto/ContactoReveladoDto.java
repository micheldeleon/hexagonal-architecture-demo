package com.example.demo.adapters.in.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactoReveladoDto {
    private UUID id;
    private UUID postId;
    private Long usuarioInteresadoId;
    private Long autorPostId;
    private String telefonoRevelado;
    private LocalDateTime fechaContacto;
}
