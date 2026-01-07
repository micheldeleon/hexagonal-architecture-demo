package com.example.demo.adapters.out.persistence.jpa.entities;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reputations", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "organizer_id", "tournament_id"}))
@Data
@NoArgsConstructor
public class ReputationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;  // ID del usuario que califica
    
    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;  // ID del organizador calificado
    
    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;  // ID del torneo
    
    @Column(nullable = false)
    private int score;  // 1-5 estrellas
    
    @Column(length = 500)
    private String comment;  // Comentario opcional
    
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
    }
}
