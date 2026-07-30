package com.tutorneo.core.domain.models;

import java.time.LocalDateTime;
//import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment { // Pago
    private Long id;
    private User user;       // usuario
    private Tournament tournament; // torneo
    private double amount;   // monto
    private String status;   // estado
    private LocalDateTime createdAt;
}

