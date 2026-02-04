package com.example.demo.adapters.out.persistence.jpa.entities;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    private String name; // nombre
    private String lastName; // apellido
    @Column(unique = true) // email
    private String email;
    private String password;
    @Column(unique = true)
    private String googleSub;
    private Date dateOfBirth; // fechaNacimiento
    @Column(unique = true)
    private String nationalId; // ci
    private String phoneNumber; // celular
    private String address;
    private String profileImageUrl; // URL de la imagen de perfil

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "delete_reason", length = 500)
    private String deleteReason;

    @ManyToOne
    private DepartmentEntity department;
    @ManyToMany
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "user_id", "role_id" }))
    private List<RoleEntity> roles;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
