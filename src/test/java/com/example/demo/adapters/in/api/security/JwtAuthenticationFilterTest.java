package com.example.demo.adapters.in.api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

class JwtAuthenticationFilterTest {

    @Test
    void attemptAuthentication_rejectsMissingUsernameOrPassword() {
        AuthenticationManager authManager = Mockito.mock(AuthenticationManager.class);
        JwtUtil jwtUtil = new JwtUtil();
        UserRepositoryPort userRepositoryPort = Mockito.mock(UserRepositoryPort.class);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authManager, jwtUtil, userRepositoryPort);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
        request.setContentType("application/json");
        request.setContent("{\"username\":\"ana@example.com\"}".getBytes());

        assertThatThrownBy(() -> filter.attemptAuthentication(request, new MockHttpServletResponse()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing");
    }

    @Test
    void successfulAuthentication_writesTokenAndUserBody() throws IOException, ServletException {
        AuthenticationManager authManager = Mockito.mock(AuthenticationManager.class);
        JwtUtil jwtUtil = new JwtUtil();
        UserRepositoryPort userRepositoryPort = Mockito.mock(UserRepositoryPort.class);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authManager, jwtUtil, userRepositoryPort);

        User domainUser = TestDataFactory.validUser(1L);
        domainUser.setEmail("ana@example.com");
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(domainUser));

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
                "ana@example.com",
                "N/A",
                java.util.List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Authentication authResult = new UsernamePasswordAuthenticationToken(principal, null,
                principal.getAuthorities());

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.successfulAuthentication(request, response, chain, authResult);

        assertThat(response.getHeader("Authorization")).startsWith("Bearer ");
        String json = response.getContentAsString();
        var tree = new ObjectMapper().readTree(json);
        assertThat(tree.get("token").asText()).isNotBlank();
        assertThat(tree.get("user").get("email").asText()).isEqualTo("ana@example.com");
    }

    @Test
    void unsuccessfulAuthentication_writes401Json() throws IOException, ServletException {
        AuthenticationManager authManager = Mockito.mock(AuthenticationManager.class);
        JwtUtil jwtUtil = new JwtUtil();
        UserRepositoryPort userRepositoryPort = Mockito.mock(UserRepositoryPort.class);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authManager, jwtUtil, userRepositoryPort);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.unsuccessfulAuthentication(request, response,
                new org.springframework.security.core.AuthenticationException("bad") {
                });

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString()).contains("Error en la autenticacion");
    }
}

