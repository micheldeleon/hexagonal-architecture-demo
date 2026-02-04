package com.example.demo.core.ports.in;

public interface RestoreUserPort {
    void restore(Long targetUserId, Long adminUserId);
}

