package com.example.demo.core.application.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.adapters.out.persistence.jpa.entities.UserEntity;
import com.example.demo.adapters.out.persistence.jpa.interfaces.UserRepositoryJpa;
import com.example.demo.core.ports.in.ChangePasswordPort;

public class ChangePasswordUseCase implements ChangePasswordPort {
    private final UserRepositoryJpa userRepositoryJpa;
    private final PasswordEncoder passwordEncoder;
    
    public ChangePasswordUseCase(UserRepositoryJpa userRepositoryJpa, PasswordEncoder passwordEncoder) {
        this.userRepositoryJpa = userRepositoryJpa;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        // Buscar el usuario
        UserEntity userEntity = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(currentPassword, userEntity.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        
        // Verificar que la nueva contraseña no sea igual a la actual
        if (passwordEncoder.matches(newPassword, userEntity.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la actual");
        }
        
        // Encriptar y actualizar la nueva contraseña
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepositoryJpa.save(userEntity);
    }
}
