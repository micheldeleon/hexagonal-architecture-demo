package com.tutorneo.adapters.out.persistence.jpa.entities;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teams", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "discipline_id")
    private Long disciplineId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
