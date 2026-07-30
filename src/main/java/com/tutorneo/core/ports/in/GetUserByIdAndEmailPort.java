package com.tutorneo.core.ports.in;

import com.tutorneo.core.domain.models.User;

public interface GetUserByIdAndEmailPort {
    User getUserByIdAndEmail(Long id, String Email);
}
