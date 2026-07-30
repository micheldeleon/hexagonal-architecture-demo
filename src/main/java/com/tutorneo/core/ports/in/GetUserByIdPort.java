package com.tutorneo.core.ports.in;

import com.tutorneo.core.domain.models.User;

public interface GetUserByIdPort {
    User getUserById(Long id);
}
