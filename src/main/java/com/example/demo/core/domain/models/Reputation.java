package com.example.demo.core.domain.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reputation {
    private Long id;
    private User user;              // usuario que da la reputacion
    private Organizer organizer;    // organizador que recibe la reputacion
    private Tournament tournament;  // torneo donde participó (contexto)
    private int score;              // puntaje de reputacion (1-5 estrellas)
    private String comment;         // comentario opcional
    private Date createdAt;         // fecha de creación
    
    // Constructor sin id y createdAt para nuevas reputaciones
    public Reputation(User user, Organizer organizer, Tournament tournament, int score, String comment) {
        this.user = user;
        this.organizer = organizer;
        this.tournament = tournament;
        this.score = score;
        this.comment = comment;
        this.createdAt = new Date();
    }
}
