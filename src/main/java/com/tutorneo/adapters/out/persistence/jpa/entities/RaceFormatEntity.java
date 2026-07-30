package com.tutorneo.adapters.out.persistence.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "race_formats")
@PrimaryKeyJoinColumn(name = "id") // comparte PK con formats.id (FK)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RaceFormatEntity extends FormatEntity {
    // Sin campos adicionales por ahora; se mantiene para soportar herencia TPT.
}
