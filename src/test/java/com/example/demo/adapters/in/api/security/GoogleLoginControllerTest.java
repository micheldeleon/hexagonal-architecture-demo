package com.example.demo.adapters.in.api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

class GoogleLoginControllerTest {

    @Test
    void loginWithGoogle_returns401WhenMissingAuthorizationHeader() throws Exception {
        JwtDecoder googleJwtDecoder = Mockito.mock(JwtDecoder.class);
        UserRepositoryPort userRepositoryPort = Mockito.mock(UserRepositoryPort.class);
        JpaUserDetailsService userDetailsService = Mockito.mock(JpaUserDetailsService.class);

        GoogleLoginController controller = new GoogleLoginController(
                googleJwtDecoder,
                Mockito.mock(AuthenticationManager.class),
                new JwtUtil(),
                userRepositoryPort,
                userDetailsService);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login/google");
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.loginWithGoogle(null, request, response);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Missing Authorization");
    }

    @Test
    void loginWithGoogle_returns401WhenJwtDecoderRejectsToken() throws Exception {
        JwtDecoder googleJwtDecoder = Mockito.mock(JwtDecoder.class);
        when(googleJwtDecoder.decode(anyString())).thenThrow(new JwtException("bad"));

        UserRepositoryPort userRepositoryPort = Mockito.mock(UserRepositoryPort.class);
        JpaUserDetailsService userDetailsService = Mockito.mock(JpaUserDetailsService.class);

        GoogleLoginController controller = new GoogleLoginController(
                googleJwtDecoder,
                Mockito.mock(AuthenticationManager.class),
                new JwtUtil(),
                userRepositoryPort,
                userDetailsService);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login/google");
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.loginWithGoogle("Bearer invalid", request, response);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Invalid Google ID token");
    }

    @Test
    void loginWithGoogle_returns401WhenUserNotRegistered() throws Exception {
        JwtDecoder googleJwtDecoder = Mockito.mock(JwtDecoder.class);
        Jwt jwt = Jwt.withTokenValue("google-id-token")
                .header("alg", "RS256")
                .subject("google-sub-123")
                .claim("email", "ana@example.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        when(googleJwtDecoder.decode(anyString())).thenReturn(jwt);

        UserRepositoryPort userRepositoryPort = Mockito.mock(UserRepositoryPort.class);
        when(userRepositoryPort.findByGoogleSub("google-sub-123")).thenReturn(Optional.empty());
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.empty());

        JpaUserDetailsService userDetailsService = Mockito.mock(JpaUserDetailsService.class);

        GoogleLoginController controller = new GoogleLoginController(
                googleJwtDecoder,
                Mockito.mock(AuthenticationManager.class),
                new JwtUtil(),
                userRepositoryPort,
                userDetailsService);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login/google");
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.loginWithGoogle("Bearer google-id-token", request, response);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("User not registered");
    }

    @Test
    void loginWithGoogle_returnsAuthDataWhenValidTokenAndUserExists() throws Exception {
        JwtDecoder googleJwtDecoder = Mockito.mock(JwtDecoder.class);
        Jwt jwt = Jwt.withTokenValue("google-id-token")
                .header("alg", "RS256")
                .subject("google-sub-123")
                .claim("email", "ana@example.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        when(googleJwtDecoder.decode(anyString())).thenReturn(jwt);

        User domainUser = TestDataFactory.validUser(1L);
        domainUser.setEmail("ana@example.com");

        UserRepositoryPort userRepositoryPort = Mockito.mock(UserRepositoryPort.class);
        when(userRepositoryPort.findByGoogleSub("google-sub-123")).thenReturn(Optional.empty());
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(domainUser));
        doNothing().when(userRepositoryPort).linkGoogleSub(1L, "google-sub-123");

        JpaUserDetailsService userDetailsService = Mockito.mock(JpaUserDetailsService.class);
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
                "ana@example.com",
                "N/A",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetailsService.loadUserByUsername("ana@example.com")).thenReturn(principal);

        GoogleLoginController controller = new GoogleLoginController(
                googleJwtDecoder,
                Mockito.mock(AuthenticationManager.class),
                new JwtUtil(),
                userRepositoryPort,
                userDetailsService);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login/google");
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.loginWithGoogle("Bearer google-id-token", request, response);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader("Authorization")).startsWith("Bearer ");

        var tree = new ObjectMapper().readTree(response.getContentAsString());
        assertThat(tree.get("token").asText()).isNotBlank();
        assertThat(tree.get("user").get("email").asText()).isEqualTo("ana@example.com");
    }
}

