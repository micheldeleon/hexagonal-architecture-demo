package com.example.demo.adapters.in.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactMessageRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede tener mas de 120 caracteres")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    @Size(max = 254, message = "El email no puede tener mas de 254 caracteres")
    private String email;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 4000, message = "El mensaje no puede tener mas de 4000 caracteres")
    private String message;
}

