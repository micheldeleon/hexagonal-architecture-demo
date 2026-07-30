package com.tutorneo.core.application.usecase;

import java.util.List;


import com.tutorneo.core.domain.models.User;
import com.tutorneo.core.ports.in.ListUsersPort;
import com.tutorneo.core.ports.out.UserRepositoryPort;
public class ListUsersUseCase implements ListUsersPort{
    private final UserRepositoryPort userRepository;

    public ListUsersUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }
    
}
