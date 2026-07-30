package com.tutorneo.core.ports.out;

import java.util.List;

import com.tutorneo.core.domain.models.AdminUserSummary;

public interface UserAdminReadPort {
    List<AdminUserSummary> listUsers(boolean includeDeleted);
}

