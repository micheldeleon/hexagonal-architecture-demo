package com.example.demo.adapters.out.persistence.jpa.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.adapters.out.persistence.jpa.entities.OrganizerRoleRequestEntity;

@Repository
public interface OrganizerRoleRequestRepositoryJpa extends JpaRepository<OrganizerRoleRequestEntity, Long> {
    Optional<OrganizerRoleRequestEntity> findFirstByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
    Optional<OrganizerRoleRequestEntity> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
    List<OrganizerRoleRequestEntity> findByStatusOrderByCreatedAtDesc(String status);
}

