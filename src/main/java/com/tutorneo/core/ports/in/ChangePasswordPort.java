package com.tutorneo.core.ports.in;

public interface ChangePasswordPort {
    void changePassword(Long userId, String currentPassword, String newPassword);
}
