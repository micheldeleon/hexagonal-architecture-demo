package com.tutorneo.core.application.usecase;

import com.tutorneo.core.domain.models.User;
import com.tutorneo.core.ports.in.GetUserByIdPort;
import com.tutorneo.core.ports.out.UserRepositoryPort;

public class GetUserByIdUseCase implements GetUserByIdPort {

    private final UserRepositoryPort repo;

    public GetUserByIdUseCase(UserRepositoryPort userRepository) {
        this.repo = userRepository;
    }

    @Override
    public User getUserById(Long id) {
        if (id == 0 || id == null)
            throw new RuntimeException("Id invalido");
        User user = repo.findById(id);
        return user;
    }

    

}
