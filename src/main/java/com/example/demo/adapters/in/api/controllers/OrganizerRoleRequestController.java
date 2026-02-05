package com.example.demo.adapters.in.api.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.adapters.in.api.dto.CreateOrganizerRoleRequestDto;
import com.example.demo.adapters.in.api.dto.OrganizerRoleRequestDto;
import com.example.demo.adapters.in.api.mappers.OrganizerRoleRequestDtoMapper;
import com.example.demo.core.domain.models.OrganizerRoleRequest;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.GetMyOrganizerRoleRequestPort;
import com.example.demo.core.ports.in.RequestOrganizerRolePort;
import com.example.demo.core.ports.out.UserRepositoryPort;

@RestController
@RequestMapping("/api/users/organizer-requests")
public class OrganizerRoleRequestController {

    private final RequestOrganizerRolePort requestOrganizerRolePort;
    private final GetMyOrganizerRoleRequestPort getMyOrganizerRoleRequestPort;
    private final UserRepositoryPort userRepositoryPort;

    public OrganizerRoleRequestController(
            RequestOrganizerRolePort requestOrganizerRolePort,
            GetMyOrganizerRoleRequestPort getMyOrganizerRoleRequestPort,
            UserRepositoryPort userRepositoryPort) {
        this.requestOrganizerRolePort = requestOrganizerRolePort;
        this.getMyOrganizerRoleRequestPort = getMyOrganizerRoleRequestPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody(required = false) CreateOrganizerRoleRequestDto body, Authentication authentication) {
        User user = resolveUser(authentication);
        try {
            OrganizerRoleRequest created = requestOrganizerRolePort.request(user.getId(), body == null ? null : body.getMessage());
            OrganizerRoleRequestDto dto = OrganizerRoleRequestDtoMapper.toDto(created);
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        User user = resolveUser(authentication);
        Optional<OrganizerRoleRequest> latest = getMyOrganizerRoleRequestPort.getLatest(user.getId());
        return latest.<ResponseEntity<?>>map(req -> ResponseEntity.ok(OrganizerRoleRequestDtoMapper.toDto(req)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No organizer role request found")));
    }

    private User resolveUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        String email = authentication.getName();
        return userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}

