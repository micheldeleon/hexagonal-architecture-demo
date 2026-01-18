package com.example.demo.core.ports.out;

import java.util.Optional;

import com.example.demo.core.domain.models.User;

public interface UserRepositoryPort extends RepositoryPort<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleSub(String googleSub);

    void update(User user);
    void addRole(Long userId, String roleName);
    void linkGoogleSub(Long userId, String googleSub);
}
