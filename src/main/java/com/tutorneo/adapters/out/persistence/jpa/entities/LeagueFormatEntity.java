package com.tutorneo.adapters.out.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "league_formats")
@PrimaryKeyJoinColumn(name = "id") // usa el mismo id que formats.id como FK/PK
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeagueFormatEntity extends FormatEntity {

    @Column(name = "win_points")
    private int winPoints;

    @Column(name = "draw_points")
    private int drawPoints;

    @Column(name = "loss_points")
    private int lossPoints;

    @Column(name = "has_tiebreak")
    private boolean hasTiebreak;
}
