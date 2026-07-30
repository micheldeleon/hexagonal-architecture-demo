package com.tutorneo.adapters.out.persistence.jpa.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutorneo.adapters.out.persistence.jpa.entities.ParticipantEntity;

@Repository
public interface ParticipantRepositoryJpa extends JpaRepository<ParticipantEntity, Long> {

    java.util.Optional<ParticipantEntity> findByNationalId(String nationalId);
}
