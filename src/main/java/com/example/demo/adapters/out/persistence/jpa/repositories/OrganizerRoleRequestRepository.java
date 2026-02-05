package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.adapters.out.persistence.jpa.interfaces.OrganizerRoleRequestRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.mappers.OrganizerRoleRequestMapper;
import com.example.demo.core.domain.models.OrganizerRoleRequest;
import com.example.demo.core.domain.models.OrganizerRoleRequestStatus;
import com.example.demo.core.ports.out.OrganizerRoleRequestRepositoryPort;

@Repository
public class OrganizerRoleRequestRepository implements OrganizerRoleRequestRepositoryPort {

    private final OrganizerRoleRequestRepositoryJpa organizerRoleRequestRepositoryJpa;

    public OrganizerRoleRequestRepository(OrganizerRoleRequestRepositoryJpa organizerRoleRequestRepositoryJpa) {
        this.organizerRoleRequestRepositoryJpa = organizerRoleRequestRepositoryJpa;
    }

    @Override
    @Transactional
    public OrganizerRoleRequest save(OrganizerRoleRequest request) {
        var entity = OrganizerRoleRequestMapper.toEntity(request);
        var saved = organizerRoleRequestRepositoryJpa.save(entity);
        return OrganizerRoleRequestMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizerRoleRequest> findById(Long id) {
        return organizerRoleRequestRepositoryJpa.findById(id).map(OrganizerRoleRequestMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizerRoleRequest> findLatestByUserId(Long userId) {
        return organizerRoleRequestRepositoryJpa.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .map(OrganizerRoleRequestMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizerRoleRequest> findPendingByUserId(Long userId) {
        return organizerRoleRequestRepositoryJpa.findFirstByUserIdAndStatusOrderByCreatedAtDesc(userId, OrganizerRoleRequestStatus.PENDING.name())
                .map(OrganizerRoleRequestMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizerRoleRequest> findByStatus(OrganizerRoleRequestStatus status) {
        return organizerRoleRequestRepositoryJpa.findByStatusOrderByCreatedAtDesc(status.name()).stream()
                .map(OrganizerRoleRequestMapper::toDomain)
                .toList();
    }
}

