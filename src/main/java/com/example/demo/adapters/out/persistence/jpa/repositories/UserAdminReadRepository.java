package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.adapters.out.persistence.jpa.entities.UserEntity;
import com.example.demo.adapters.out.persistence.jpa.interfaces.TournamentParticipantRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.TournamentRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.UserRepositoryJpa;
import com.example.demo.core.domain.models.AdminUserSummary;
import com.example.demo.core.ports.out.UserAdminReadPort;

@Repository
public class UserAdminReadRepository implements UserAdminReadPort {

    private final UserRepositoryJpa userRepositoryJpa;
    private final TournamentRepositoryJpa tournamentRepositoryJpa;
    private final TournamentParticipantRepositoryJpa tournamentParticipantRepositoryJpa;

    public UserAdminReadRepository(
            UserRepositoryJpa userRepositoryJpa,
            TournamentRepositoryJpa tournamentRepositoryJpa,
            TournamentParticipantRepositoryJpa tournamentParticipantRepositoryJpa) {
        this.userRepositoryJpa = userRepositoryJpa;
        this.tournamentRepositoryJpa = tournamentRepositoryJpa;
        this.tournamentParticipantRepositoryJpa = tournamentParticipantRepositoryJpa;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserSummary> listUsers(boolean includeDeleted) {
        List<UserEntity> users = includeDeleted
                ? userRepositoryJpa.findAllBy()
                : userRepositoryJpa.findAllByDeletedAtIsNull();

        if (users.isEmpty()) {
            return List.of();
        }

        List<Long> userIds = users.stream().map(UserEntity::getId).toList();

        Map<Long, Long> tournamentsByOrganizer = toCountMapSafe(
                tournamentRepositoryJpa.countByOrganizerIds(userIds));
        Map<Long, Long> participationsByUser = toCountMapSafe(
                tournamentParticipantRepositoryJpa.countByUserIds(userIds));

        return users.stream().map(u -> new AdminUserSummary(
                u.getId(),
                u.getName(),
                u.getLastName(),
                u.getEmail(),
                u.getCreatedAt(),
                u.getDeletedAt(),
                u.getDeletedBy(),
                u.getDeleteReason(),
                u.getRoles() == null
                        ? List.of()
                        : u.getRoles().stream()
                                .map(r -> r.getName())
                                .sorted()
                                .toList(),
                tournamentsByOrganizer.getOrDefault(u.getId(), 0L),
                participationsByUser.getOrDefault(u.getId(), 0L)
        )).toList();
    }

    private static Map<Long, Long> toCountMapSafe(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, Long> out = new HashMap<>();
        for (Object[] row : rows) {
            if (row == null || row.length < 2 || row[0] == null || row[1] == null) {
                continue;
            }
            out.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }
        return out;
    }
}

