package com.example.demo.adapters.out.persistence.jpa.interfaces;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.adapters.out.persistence.jpa.entities.UserEntity;

public interface UserRepositoryJpa extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByGoogleSub(String googleSub);
}
