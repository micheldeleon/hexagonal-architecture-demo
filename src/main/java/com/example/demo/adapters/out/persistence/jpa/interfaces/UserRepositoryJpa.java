package com.example.demo.adapters.out.persistence.jpa.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.adapters.out.persistence.jpa.entities.UserEntity;

public interface UserRepositoryJpa extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmailAndDeletedAtIsNull(String email);
    Optional<UserEntity> findByGoogleSub(String googleSub);
    Optional<UserEntity> findByGoogleSubAndDeletedAtIsNull(String googleSub);
    Optional<UserEntity> findByIdAndDeletedAtIsNull(Long id);

    @EntityGraph(attributePaths = "roles")
    List<UserEntity> findAllBy();

    @EntityGraph(attributePaths = "roles")
    List<UserEntity> findAllByDeletedAtIsNull();
}
