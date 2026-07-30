package com.tutorneo.adapters.in.api.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutorneo.adapters.in.api.dto.ContactMessageRequest;
import com.tutorneo.core.ports.in.SendContactMessagePort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final SendContactMessagePort sendContactMessagePort;

    public ContactController(SendContactMessagePort sendContactMessagePort) {
        this.sendContactMessagePort = sendContactMessagePort;
    }

    @PostMapping
    public ResponseEntity<?> send(@Valid @RequestBody ContactMessageRequest request) {
        try {
            sendContactMessagePort.send(request.getName(), request.getEmail(), request.getMessage());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Mensaje enviado"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}

