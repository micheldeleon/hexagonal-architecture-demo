package com.example.demo.core.ports.in;

public interface ChangePasswordPort {
    void changePassword(Long userId, String currentPassword, String newPassword);
}
