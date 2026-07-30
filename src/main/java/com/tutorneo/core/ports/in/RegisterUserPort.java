package com.tutorneo.core.ports.in;

import com.tutorneo.core.domain.models.User;

public interface RegisterUserPort {
    void registerUser(User user);
}
