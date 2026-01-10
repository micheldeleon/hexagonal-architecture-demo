package com.example.demo.adapters.in.api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

class JwtValidationFilterTest {

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_skipsValidationForPublicEndpoints() throws ServletException, IOException {
        JwtUtil jwtUtil = new JwtUtil();
        JwtValidationFilter filter = new JwtValidationFilter(Mockito.mock(AuthenticationManager.class), jwtUtil);
        FilterChain chain = Mockito.mock(FilterChain.class);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/tournaments/public");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_returns401OnInvalidTokenForProtectedEndpoint() throws ServletException, IOException {
        JwtUtil jwtUtil = new JwtUtil();
        JwtValidationFilter filter = new JwtValidationFilter(Mockito.mock(AuthenticationManager.class), jwtUtil);
        FilterChain chain = Mockito.mock(FilterChain.class);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/tournaments/10/register");
        request.addHeader("Authorization", "Bearer invalid.token.value");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(401);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_setsAuthenticationOnValidToken() throws ServletException, IOException {
        JwtUtil jwtUtil = new JwtUtil();
        String token = Jwts.builder()
                .subject("ana@example.com")
                .claim("authorities", List.of("ROLE_USER"))
                .signWith(jwtUtil.getSecretKey())
                .compact();

        JwtValidationFilter filter = new JwtValidationFilter(Mockito.mock(AuthenticationManager.class), jwtUtil);
        FilterChain chain = Mockito.mock(FilterChain.class);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/tournaments/10/register");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("ana@example.com");
    }
}

