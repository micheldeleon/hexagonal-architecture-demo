package com.example.demo.adapters.out.persistence.jpa.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comentarios", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "post_id", nullable = false)
    private UUID postId;
    
    @Column(name = "autor_id", nullable = false)
    private Long autorId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    @Column(name = "comentario_padre_id")
    private UUID comentarioPadreId;
    
    @Column(name = "fecha_creacion")
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
