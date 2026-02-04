package com.example.demo.core.ports.in;

public interface DeactivateUserPort {
    void deactivate(Long targetUserId, Long adminUserId, String reason);
}

