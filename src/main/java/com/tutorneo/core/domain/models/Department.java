package com.tutorneo.core.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    private Long id;
    private String name;

    public Department(Long id) {
        this.id = id;
    }
}
