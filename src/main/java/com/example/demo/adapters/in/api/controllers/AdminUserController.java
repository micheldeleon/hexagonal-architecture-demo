package com.example.demo.adapters.in.api.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adapters.in.api.dto.AdminUserDto;
import com.example.demo.adapters.in.api.dto.DeactivateUserRequest;
import com.example.demo.adapters.in.api.mappers.AdminUserMapperDtos;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.DeactivateUserPort;
import com.example.demo.core.ports.in.ListUsersAdminPort;
import com.example.demo.core.ports.in.RestoreUserPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final ListUsersAdminPort listUsersAdminPort;
    private final DeactivateUserPort deactivateUserPort;
    private final RestoreUserPort restoreUserPort;
    private final UserRepositoryPort userRepositoryPort;

    public AdminUserController(
            ListUsersAdminPort listUsersAdminPort,
            DeactivateUserPort deactivateUserPort,
            RestoreUserPort restoreUserPort,
            UserRepositoryPort userRepositoryPort) {
        this.listUsersAdminPort = listUsersAdminPort;
        this.deactivateUserPort = deactivateUserPort;
        this.restoreUserPort = restoreUserPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @GetMapping
    public List<AdminUserDto> listUsers(@RequestParam(defaultValue = "true") boolean includeDeleted) {
        return listUsersAdminPort.listUsers(includeDeleted).stream()
                .map(AdminUserMapperDtos::toAdminDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivateUser(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @RequestBody(required = false) DeactivateUserRequest body,
            Authentication authentication) {
        Long adminId = resolveAdminId(authentication);
        String finalReason = (body != null && body.getReason() != null) ? body.getReason() : reason;
        try {
            deactivateUserPort.deactivate(id, adminId, finalReason);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(mapStatus(e)).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<?> restoreUser(@PathVariable Long id, Authentication authentication) {
        Long adminId = resolveAdminId(authentication);
        try {
            restoreUserPort.restore(id, adminId);
            return ResponseEntity.ok(Map.of("message", "User restored"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(mapStatus(e)).body(e.getMessage());
        }
    }

    private static HttpStatus mapStatus(IllegalArgumentException e) {
        String message = e.getMessage();
        if (message != null && message.toLowerCase().contains("not found")) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private Long resolveAdminId(Authentication authentication) {
        if (authentication == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        String email = authentication.getName();
        User admin = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return admin.getId();
    }
}
