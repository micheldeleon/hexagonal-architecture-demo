package com.tutorneo.adapters.in.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String email;
    private String fullName;

    public UserResponseDTO(Long id, String email, String name, String lastName) {
        this.id = id;
        this.email = email;
        this.fullName = name + " " + lastName;
    }
}
