package com.example.demo.adapters.in.api.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret:${JWT_SECRET:}}")
    private String jwtSecret;

    private static final String FALLBACK_SECRET = "change-me-super-long-secret-key-32chars!!";

    public SecretKey getSecretKey() {
        String secretToUse = (jwtSecret != null && jwtSecret.length() >= 32)
                ? jwtSecret
                : FALLBACK_SECRET;
        return Keys.hmacShaKeyFor(secretToUse.getBytes(StandardCharsets.UTF_8));
    }
}
