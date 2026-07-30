package com.tutorneo.core.domain.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Sponsor {
    private Long id;
    private Tournament tournament; // null cuando es global
    private String name;
    private String logoUrl;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
