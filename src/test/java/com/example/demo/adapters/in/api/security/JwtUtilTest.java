package com.example.demo.adapters.in.api.security;

import static org.assertj.core.api.Assertions.assertThat;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    @Test
    void getSecretKey_usesFallbackWhenSecretTooShort() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "short");
        SecretKey key = jwtUtil.getSecretKey();
        assertThat(key).isNotNull();
        assertThat(key.getEncoded()).isNotEmpty();
    }

    @Test
    void getSecretKey_usesProvidedSecretWhenLongEnough() {
        JwtUtil fallback = new JwtUtil();
        ReflectionTestUtils.setField(fallback, "jwtSecret", "");
        byte[] fallbackBytes = fallback.getSecretKey().getEncoded();

        JwtUtil provided = new JwtUtil();
        ReflectionTestUtils.setField(provided, "jwtSecret", "01234567890123456789012345678901");
        byte[] providedBytes = provided.getSecretKey().getEncoded();

        assertThat(providedBytes).isNotEmpty();
        assertThat(providedBytes).isNotEqualTo(fallbackBytes);
    }
}

