package com.example.demo.adapters.out.persistence.jpa.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false, length = 200)
    private String titulo;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    @Column(name = "autor_id", nullable = false)
    private Long autorId;
    
    @Column(name = "tipo_post", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TipoPostEntity tipoPost;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoPostEntity estado;
    
    @Column(length = 50)
    private String deporte;
    
    @Column(length = 200)
    private String ubicacion;
    
    @Column(name = "fecha_creacion")
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
