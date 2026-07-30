package com.tutorneo.core.domain.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceResult {
    private Long id;
    private Long tournamentId;
    private Long teamId;
    private String teamName;
    private Long timeMillis;
    private Integer position;
    private Date createdAt;
    private Date updatedAt;
}
