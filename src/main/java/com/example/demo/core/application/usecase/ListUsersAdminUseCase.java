package com.example.demo.core.application.usecase;

import java.util.List;

import com.example.demo.core.domain.models.AdminUserSummary;
import com.example.demo.core.ports.in.ListUsersAdminPort;
import com.example.demo.core.ports.out.UserAdminReadPort;

public class ListUsersAdminUseCase implements ListUsersAdminPort {

    private final UserAdminReadPort userAdminReadPort;

    public ListUsersAdminUseCase(UserAdminReadPort userAdminReadPort) {
        this.userAdminReadPort = userAdminReadPort;
    }

    @Override
    public List<AdminUserSummary> listUsers(boolean includeDeleted) {
        return userAdminReadPort.listUsers(includeDeleted);
    }
}
