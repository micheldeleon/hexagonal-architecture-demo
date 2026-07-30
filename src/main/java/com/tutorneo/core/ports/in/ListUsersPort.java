package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.core.domain.models.User;

public interface ListUsersPort {
    List<User> listUsers();
}
