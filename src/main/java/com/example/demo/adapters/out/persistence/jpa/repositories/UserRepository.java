package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.example.demo.adapters.out.persistence.jpa.entities.DepartmentEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.RoleEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.UserEntity;
import com.example.demo.adapters.out.persistence.jpa.interfaces.DepartmentRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.RoleRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.UserRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.mappers.DepartmentMapper;
import com.example.demo.adapters.out.persistence.jpa.mappers.UserMapper;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.out.UserRepositoryPort;

import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepository implements UserRepositoryPort {

    private final UserRepositoryJpa userRepositoryJpa;
    private final RoleRepositoryJpa roleRepositoryJpa;
    private final DepartmentRepositoryJpa departmentRepositoryJpa;
    private final PasswordEncoder passwordEncoder;

    public UserRepository(UserRepositoryJpa userRepositoryJpa, RoleRepositoryJpa roleRepositoryJpa,
            PasswordEncoder passwordEncoder, DepartmentRepositoryJpa dRepositoryJpa) {
        this.userRepositoryJpa = userRepositoryJpa;
        this.roleRepositoryJpa = roleRepositoryJpa;
        this.passwordEncoder = passwordEncoder;
        this.departmentRepositoryJpa = dRepositoryJpa;
    }

    @Override
    @Transactional
    public List<User> findAll() {
        Iterable<UserEntity> userEntities = userRepositoryJpa.findAllByDeletedAtIsNull();
        List<User> users = new ArrayList<>();
        userEntities.forEach(user -> users.add(UserMapper.toDomain(user)));
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllIncludingDeleted() {
        Iterable<UserEntity> userEntities = userRepositoryJpa.findAll();
        List<User> users = new ArrayList<>();
        userEntities.forEach(user -> users.add(UserMapper.toDomain(user)));
        return users;
    }

    @Override
    @Transactional
    public void save(User entity) {
        if (userRepositoryJpa.findByEmail(entity.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        DepartmentEntity dep = null;
        var depIterator = departmentRepositoryJpa.findAll().iterator();
        if (depIterator.hasNext()) {
            dep = depIterator.next();
        } else {
            dep = departmentRepositoryJpa.save(new DepartmentEntity(null, "Sin departamento"));
        }
        entity.setDepartment(DepartmentMapper.toDomain(dep));
        Optional<RoleEntity> userRole = roleRepositoryJpa.findByName("ROLE_USER");
        List<RoleEntity> roles = new ArrayList<>();
        userRole.ifPresent(roles::add);
        UserEntity userEntity = UserMapper
                .toEntity(entity, roles);
        userEntity.setRoles(roles);
        // departmentRepositoryJpa.findById(0);
        userRepositoryJpa.save(userEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        UserEntity entity = userRepositoryJpa.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserMapper.toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepositoryJpa.findByEmailAndDeletedAtIsNull(email)
                .map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByGoogleSub(String googleSub) {
        return userRepositoryJpa.findByGoogleSubAndDeletedAtIsNull(googleSub)
                .map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmailIncludingDeleted(String email) {
        return userRepositoryJpa.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByIdIncludingDeleted(Long id) {
        return userRepositoryJpa.findById(id)
                .map(UserMapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    @Transactional
    public void update(User user) {
        User lastUser = findByEmail(user.getEmail()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        lastUser.profileUpdate(user);
        List<RoleEntity> roles = userRepositoryJpa.findById(lastUser.getId()).get().getRoles();
        userRepositoryJpa.save(UserMapper.toEntity(lastUser, roles));
    }

    @Override
    @Transactional
    public void addRole(Long userId, String roleName) {
        UserEntity userEntity = userRepositoryJpa.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        RoleEntity roleEntity = roleRepositoryJpa.findByName(roleName).orElseThrow(() -> new IllegalArgumentException("Role not found"));
        if (!userEntity.getRoles().contains(roleEntity)) {
            userEntity.getRoles().add(roleEntity);
            userRepositoryJpa.save(userEntity);
        }
    }

    @Override
    @Transactional
    public void linkGoogleSub(Long userId, String googleSub) {
        UserEntity userEntity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String existing = userEntity.getGoogleSub();
        if (existing == null || existing.isBlank()) {
            userRepositoryJpa.findByGoogleSub(googleSub).ifPresent(other -> {
                if (!other.getId().equals(userId)) {
                    throw new IllegalArgumentException("Google account already linked to another user");
                }
            });
            userEntity.setGoogleSub(googleSub);
            userRepositoryJpa.save(userEntity);
            return;
        }

        if (!existing.equals(googleSub)) {
            throw new IllegalArgumentException("User already linked to a different Google account");
        }
    }

    @Override
    @Transactional
    public void deactivate(Long userId, Long adminUserId, String reason) {
        UserEntity userEntity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (userEntity.getDeletedAt() != null) {
            throw new IllegalStateException("User already deactivated");
        }
        userEntity.setDeletedAt(Instant.now());
        userEntity.setDeletedBy(adminUserId);
        userEntity.setDeleteReason(reason);
        userRepositoryJpa.save(userEntity);
    }

    @Override
    @Transactional
    public void restore(Long userId, Long adminUserId) {
        UserEntity userEntity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (userEntity.getDeletedAt() == null) {
            return;
        }
        userEntity.setDeletedAt(null);
        userEntity.setDeletedBy(null);
        userEntity.setDeleteReason(null);
        userRepositoryJpa.save(userEntity);
    }
    
}
