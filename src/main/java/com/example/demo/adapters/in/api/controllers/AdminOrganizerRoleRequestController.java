package com.example.demo.adapters.in.api.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.adapters.in.api.dto.ApproveOrganizerRoleRequestDto;
import com.example.demo.adapters.in.api.dto.OrganizerRoleRequestDto;
import com.example.demo.adapters.in.api.dto.RejectOrganizerRoleRequestDto;
import com.example.demo.adapters.in.api.mappers.OrganizerRoleRequestDtoMapper;
import com.example.demo.core.domain.models.OrganizerRoleRequestStatus;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.ListOrganizerRoleRequestsPort;
import com.example.demo.core.ports.in.ReviewOrganizerRoleRequestPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

@RestController
@RequestMapping("/api/admin/organizer-requests")
public class AdminOrganizerRoleRequestController {

    private final ListOrganizerRoleRequestsPort listOrganizerRoleRequestsPort;
    private final ReviewOrganizerRoleRequestPort reviewOrganizerRoleRequestPort;
    private final UserRepositoryPort userRepositoryPort;

    public AdminOrganizerRoleRequestController(
            ListOrganizerRoleRequestsPort listOrganizerRoleRequestsPort,
            ReviewOrganizerRoleRequestPort reviewOrganizerRoleRequestPort,
            UserRepositoryPort userRepositoryPort) {
        this.listOrganizerRoleRequestsPort = listOrganizerRoleRequestsPort;
        this.reviewOrganizerRoleRequestPort = reviewOrganizerRoleRequestPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "PENDING") String status) {
        OrganizerRoleRequestStatus parsed;
        try {
            parsed = OrganizerRoleRequestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid status. Use PENDING, APPROVED, or REJECTED"));
        }

        List<OrganizerRoleRequestDto> out = listOrganizerRoleRequestsPort.listByStatus(parsed).stream()
                .map(OrganizerRoleRequestDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            @RequestBody(required = false) ApproveOrganizerRoleRequestDto body,
            Authentication authentication) {
        Long adminId = resolveAdminId(authentication);
        try {
            var approved = reviewOrganizerRoleRequestPort.approve(id, adminId, body == null ? null : body.getNote());
            return ResponseEntity.ok(OrganizerRoleRequestDtoMapper.toDto(approved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(mapStatus(e)).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            @RequestBody RejectOrganizerRoleRequestDto body,
            Authentication authentication) {
        Long adminId = resolveAdminId(authentication);
        try {
            var rejected = reviewOrganizerRoleRequestPort.reject(id, adminId, body == null ? null : body.getReason());
            return ResponseEntity.ok(OrganizerRoleRequestDtoMapper.toDto(rejected));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(mapStatus(e)).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    private Long resolveAdminId(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        String email = authentication.getName();
        User admin = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return admin.getId();
    }

    private static HttpStatus mapStatus(IllegalArgumentException e) {
        String message = e.getMessage();
        if (message != null && message.toLowerCase().contains("not found")) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }
}
