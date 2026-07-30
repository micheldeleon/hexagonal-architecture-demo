package com.tutorneo.core.application.usecase;

import java.util.Optional;

import com.tutorneo.core.domain.models.User;
import com.tutorneo.core.ports.in.UpdateProfilePort;
import com.tutorneo.core.ports.out.UserRepositoryPort;

public class UpdateUserUseCase implements UpdateProfilePort {

    private final UserRepositoryPort repo;

    public UpdateUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.repo = userRepositoryPort;
    }
    //Prueba de comentario
    @Override
    public void completion(User updatedUser) {
        if (updatedUser == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        Optional<User> existingOpt = repo.findByEmail(updatedUser.getEmail());
        User existingUser = existingOpt.orElseThrow(
                () -> new IllegalArgumentException("User not found"));

        // Aplica los cambios a la instancia existente (dominio puro)
        existingUser.profileUpdate(updatedUser);

        // Guardar los cambios
        repo.update(existingUser);
    }

}
