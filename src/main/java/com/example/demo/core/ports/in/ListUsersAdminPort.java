package com.example.demo.core.ports.in;

import java.util.List;

import com.example.demo.core.domain.models.AdminUserSummary;

public interface ListUsersAdminPort {
    List<AdminUserSummary> listUsers(boolean includeDeleted);
}
