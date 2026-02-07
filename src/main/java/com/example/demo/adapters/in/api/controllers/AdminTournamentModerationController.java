package com.example.demo.adapters.in.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adapters.in.api.dto.TournamentModerationRequest;
import com.example.demo.adapters.in.api.dto.TournamentModerationResponse;
import com.example.demo.core.ports.in.AdminDeactivateTournamentPort;
import com.example.demo.core.ports.in.AdminReactivateTournamentPort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/tournaments")
public class AdminTournamentModerationController {

    private final AdminDeactivateTournamentPort adminDeactivateTournamentPort;
    private final AdminReactivateTournamentPort adminReactivateTournamentPort;

    public AdminTournamentModerationController(
            AdminDeactivateTournamentPort adminDeactivateTournamentPort,
            AdminReactivateTournamentPort adminReactivateTournamentPort) {
        this.adminDeactivateTournamentPort = adminDeactivateTournamentPort;
        this.adminReactivateTournamentPort = adminReactivateTournamentPort;
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id,
            @Valid @RequestBody TournamentModerationRequest body,
            Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("auth_missing");
            }

            var result = adminDeactivateTournamentPort.deactivate(id, authentication.getName(), body.reason());
            return ResponseEntity.ok(new TournamentModerationResponse(
                    result.tournamentId(),
                    result.moderationStatus(),
                    result.moderatedAt(),
                    result.moderatedByAdminId(),
                    result.reason()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            if ("Torneo no encontrado".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivate(@PathVariable Long id,
            @Valid @RequestBody TournamentModerationRequest body,
            Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("auth_missing");
            }

            var result = adminReactivateTournamentPort.reactivate(id, authentication.getName(), body.reason());
            return ResponseEntity.ok(new TournamentModerationResponse(
                    result.tournamentId(),
                    result.moderationStatus(),
                    result.moderatedAt(),
                    result.moderatedByAdminId(),
                    result.reason()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            if ("Torneo no encontrado".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

