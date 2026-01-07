package com.example.demo.adapters.in.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.adapters.in.api.dto.*;
import com.example.demo.core.ports.in.GetOrganizerReputationPort;
import com.example.demo.core.ports.in.RateOrganizerPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.core.domain.models.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/organizers")
public class OrganizerController {

    private final RateOrganizerPort rateOrganizerPort;
    private final GetOrganizerReputationPort getOrganizerReputationPort;
    private final UserRepositoryPort userRepositoryPort;

    public OrganizerController(
            RateOrganizerPort rateOrganizerPort,
            GetOrganizerReputationPort getOrganizerReputationPort,
            UserRepositoryPort userRepositoryPort) {
        this.rateOrganizerPort = rateOrganizerPort;
        this.getOrganizerReputationPort = getOrganizerReputationPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    /**
     * Calificar a un organizador por su desempeño en un torneo
     * POST /api/organizers/{organizerId}/rate
     */
    @PostMapping("/{organizerId}/rate")
    public ResponseEntity<?> rateOrganizer(
            @PathVariable Long organizerId,
            @Valid @RequestBody RateOrganizerRequest request,
            Authentication authentication) {
        try {
            // Obtener el usuario autenticado
            String userEmail = authentication != null ? authentication.getName() : null;
            if (userEmail == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }

            User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Calificar
            var result = rateOrganizerPort.rate(
                user.getId(),
                organizerId,
                request.tournamentId(),
                request.score(),
                request.comment()
            );

            String message = String.format(
                "Has calificado al organizador con %d estrellas. Su nuevo promedio es %.2f (%d calificaciones)",
                result.score(),
                result.newAverage(),
                result.totalRatings()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(new RateOrganizerResponse(
                result.reputationId(),
                result.organizerId(),
                result.score(),
                result.newAverage(),
                result.totalRatings(),
                message
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al calificar: " + e.getMessage());
        }
    }

    /**
     * Obtener la reputación completa de un organizador
     * GET /api/organizers/{organizerId}/reputation
     */
    @GetMapping("/{organizerId}/reputation")
    public ResponseEntity<?> getOrganizerReputation(@PathVariable Long organizerId) {
        try {
            var result = getOrganizerReputationPort.getReputation(organizerId);
            
            // Mapear a DTO de respuesta
            RatingDistributionDto distribution = new RatingDistributionDto(
                result.distribution().fiveStars(),
                result.distribution().fourStars(),
                result.distribution().threeStars(),
                result.distribution().twoStars(),
                result.distribution().oneStar()
            );

            var recentRatings = result.recentRatings().stream()
                .map(r -> new RecentRatingDto(
                    r.userId(),
                    r.userName(),
                    r.tournamentId(),
                    r.tournamentName(),
                    r.score(),
                    r.comment(),
                    r.createdAt()
                ))
                .toList();

            return ResponseEntity.ok(new OrganizerReputationResponse(
                result.organizerId(),
                result.organizerName(),
                result.averageScore(),
                result.totalRatings(),
                distribution,
                recentRatings
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al obtener reputación: " + e.getMessage());
        }
    }
}
