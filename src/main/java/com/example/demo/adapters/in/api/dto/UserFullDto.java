package com.example.demo.adapters.in.api.dto;

import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFullDto {
    @NotNull
    private Long id;

    @NotBlank
    private String name; // nombre

    @NotBlank
    private String lastName; // apellido

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Past
    private Date dateOfBirth; // fechaNacimiento

    @NotBlank
    private String nationalId; // ci

    @NotBlank
    private String phoneNumber; // celular

    @NotBlank
    private String address;

    private Long departmentId;

    private double reputation;
    
    private String profileImageUrl; // URL de la imagen de perfil
}
