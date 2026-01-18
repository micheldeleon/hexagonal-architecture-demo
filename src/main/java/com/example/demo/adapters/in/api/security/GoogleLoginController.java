package com.example.demo.adapters.in.api.security;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/login")
public class GoogleLoginController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtDecoder googleJwtDecoder;
    private final UserRepositoryPort userRepositoryPort;
    private final JpaUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GoogleLoginController(
            @Qualifier("googleJwtDecoder") JwtDecoder googleJwtDecoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserRepositoryPort userRepositoryPort,
            JpaUserDetailsService userDetailsService) {
        this.googleJwtDecoder = googleJwtDecoder;
        this.userRepositoryPort = userRepositoryPort;
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtUtil, userRepositoryPort);
    }

    @PostMapping("/google")
    public void loginWithGoogle(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        String googleIdToken = extractBearerToken(authorizationHeader);
        if (googleIdToken == null) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization Bearer token");
            return;
        }

        Jwt jwt;
        try {
            jwt = googleJwtDecoder.decode(googleIdToken);
        } catch (JwtException ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid Google ID token");
            return;
        }

        String sub = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        if (isBlank(sub)) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Google ID token missing required claims");
            return;
        }

        User user;
        try {
            user = resolveUser(sub, email);
        } catch (IllegalArgumentException ex) {
            writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
            return;
        }

        if (user == null) {
            if (isBlank(email)) {
                writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Google ID token missing required claims");
            } else {
                writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "User not registered");
            }
            return;
        }

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            if (!(userDetails instanceof org.springframework.security.core.userdetails.User principal)) {
                writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "User not registered");
                return;
            }
            Authentication authResult = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities());
            jwtAuthenticationFilter.successfulAuthentication(request, response, null, authResult);
        } catch (UsernameNotFoundException ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "User not registered");
        }
    }

    private User resolveUser(String googleSub, String email) {
        Optional<User> bySub = userRepositoryPort.findByGoogleSub(googleSub);
        if (bySub.isPresent()) {
            return bySub.get();
        }

        if (isBlank(email)) {
            return null;
        }

        Optional<User> byEmail = userRepositoryPort.findByEmail(email);
        if (byEmail.isEmpty()) {
            return null;
        }

        User existing = byEmail.get();
        userRepositoryPort.linkGoogleSub(existing.getId(), googleSub);
        return existing;
    }

    private void writeJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("message", message)));
    }

    private static String extractBearerToken(String header) {
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
