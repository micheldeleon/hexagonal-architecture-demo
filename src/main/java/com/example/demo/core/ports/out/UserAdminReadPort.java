package com.example.demo.core.ports.out;

import java.util.List;

import com.example.demo.core.domain.models.AdminUserSummary;

public interface UserAdminReadPort {
    List<AdminUserSummary> listUsers(boolean includeDeleted);
}

