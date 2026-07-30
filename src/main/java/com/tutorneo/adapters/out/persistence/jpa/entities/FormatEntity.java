package com.tutorneo.adapters.out.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"Formats\"")
@Inheritance(strategy = InheritanceType.JOINED) // TPT para conservar datos de subclases
@Data
@NoArgsConstructor
@AllArgsConstructor

public class FormatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "generate_fixture")
    private boolean generaFixture;
}
