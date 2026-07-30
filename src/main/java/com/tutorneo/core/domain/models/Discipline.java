package com.tutorneo.core.domain.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discipline { // Disciplina

    private Long id;
    private boolean collective; // esColectivo
    private String name; // nombre
    private List<Format> formats; // formatos elegibles que van a generar fixture o no.

    public Discipline(Long id) {
        this.id = id;
    }
}
