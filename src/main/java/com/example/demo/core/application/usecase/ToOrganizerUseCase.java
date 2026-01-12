package com.example.demo.core.application.usecase;

import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.ToOrganizerPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

public class ToOrganizerUseCase implements ToOrganizerPort {

    private final UserRepositoryPort userRepositoryPort;
    public ToOrganizerUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    @Transactional
    public void toOrganizer(Long userId) {
        User user = userRepositoryPort.findById(userId);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        userRepositoryPort.addRole(userId, "ROLE_ORGANIZER");
        

    }
    
}
