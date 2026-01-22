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
@Table(name = "contactos_revelados", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactoReveladoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "post_id", nullable = false)
    private UUID postId;
    
    @Column(name = "usuario_interesado_id", nullable = false)
    private Long usuarioInteresadoId;
    
    @Column(name = "autor_post_id", nullable = false)
    private Long autorPostId;
    
    @Column(name = "telefono_revelado", length = 20)
    private String telefonoRevelado;
    
    @Column(name = "fecha_contacto")
    @CreationTimestamp
    private LocalDateTime fechaContacto;
    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
