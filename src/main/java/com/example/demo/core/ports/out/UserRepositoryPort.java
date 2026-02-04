package com.example.demo.core.ports.out;

import java.util.List;
import java.util.Optional;

import com.example.demo.core.domain.models.User;

public interface UserRepositoryPort extends RepositoryPort<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleSub(String googleSub);

    Optional<User> findByEmailIncludingDeleted(String email);
    User findByIdIncludingDeleted(Long id);
    List<User> findAllIncludingDeleted();

    void update(User user);
    void addRole(Long userId, String roleName);
    void linkGoogleSub(Long userId, String googleSub);

    void deactivate(Long userId, Long adminUserId, String reason);
    void restore(Long userId, Long adminUserId);
}
