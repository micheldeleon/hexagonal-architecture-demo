package com.tutorneo.core.ports.in;

public interface SendContactMessagePort {
    void send(String name, String email, String message);
}

