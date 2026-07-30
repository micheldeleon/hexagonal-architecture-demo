package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.core.domain.models.AdminUserSummary;

public interface ListUsersAdminPort {
    List<AdminUserSummary> listUsers(boolean includeDeleted);
}
