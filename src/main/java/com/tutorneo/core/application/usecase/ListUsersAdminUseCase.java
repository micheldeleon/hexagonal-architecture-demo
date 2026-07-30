package com.tutorneo.core.application.usecase;

import java.util.List;

import com.tutorneo.core.domain.models.AdminUserSummary;
import com.tutorneo.core.ports.in.ListUsersAdminPort;
import com.tutorneo.core.ports.out.UserAdminReadPort;

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
