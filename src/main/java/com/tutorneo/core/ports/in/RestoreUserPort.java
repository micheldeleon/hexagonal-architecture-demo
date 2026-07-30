package com.tutorneo.core.ports.in;

public interface RestoreUserPort {
    void restore(Long targetUserId, Long adminUserId);
}

