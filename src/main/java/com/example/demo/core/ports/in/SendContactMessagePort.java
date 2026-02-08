package com.example.demo.core.ports.in;

public interface SendContactMessagePort {
    void send(String name, String email, String message);
}

