package com.example.demo.adapters.in.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeactivateUserRequest {
    private String reason;
}

