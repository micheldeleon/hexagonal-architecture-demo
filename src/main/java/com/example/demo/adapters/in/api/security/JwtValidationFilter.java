package com.example.demo.adapters.in.api.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.out.UserRepositoryPort;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String PREFIX_TOKEN = "Bearer ";
    private static final String CLAIM_AUTHORITIES = "authorities";
    private static final String ERROR_BODY = "{\"message\":\"Token invalido o expirado\"}";

    private final JwtUtil jwtUtil;
    private final UserRepositoryPort userRepositoryPort;

    public JwtValidationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepositoryPort userRepositoryPort) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Permitir rutas públicas sin validación de token
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        if (isPublicEndpoint(requestPath, method)) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HEADER_AUTHORIZATION);
        if (!hasBearerToken(header)) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(PREFIX_TOKEN.length()).trim();
        Claims claims = validateToken(token);
        if (claims == null) {
            handleInvalidToken(response);
            return;
        }

        String username = claims.getSubject();
        if (username != null && isUserRevoked(username)) {
            SecurityContextHolder.clearContext();
            handleInvalidToken(response);
            return;
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authentication
                    = buildAuthentication(username, claims, request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path, String method) {
        // Endpoints completamente públicos
        if ("OPTIONS".equals(method)) return true;
        if ("/login".equals(path) && "POST".equals(method)) return true;
        if ("/login/google".equals(path) && "POST".equals(method)) return true;
        if ("/api/notifications/create".equals(path) && "POST".equals(method)) return true;
        if (path.equals("/api/users/register") && "POST".equals(method)) return true;
        if (path.equals("/api/users/profile") && "PUT".equals(method)) return true;
        if (path.equals("/api/users/by-id-and-email") && "GET".equals(method)) return true;
        if (path.equals("/api/mail/test") && "POST".equals(method)) return true;
        
        // Rutas con patrones
        if (path.startsWith("/api/disciplines") && "GET".equals(method)) return true;
        if (path.startsWith("/api/tournaments/public") && "GET".equals(method)) return true;
        if (path.startsWith("/api/tournaments/status") && "GET".equals(method)) return true;
        if (path.matches("/api/tournaments/\\d+/fixture") && "GET".equals(method)) return true;
        if (path.matches("/api/tournaments/\\d+/race/results") && "GET".equals(method)) return true;
        if (path.matches("/api/tournaments/\\d+/standings") && "GET".equals(method)) return true;
        if (path.matches("/api/tournaments/\\d+/register/team") && "POST".equals(method)) return true;
        if (path.startsWith("/api/tournaments") && "GET".equals(method)) return true;
        if (path.startsWith("/api/users") && "GET".equals(method) && !path.startsWith("/api/users/organizer-requests")) return true;
        
        return false;
    }

    private boolean hasBearerToken(String header) {
        return header != null && header.startsWith(PREFIX_TOKEN);
    }

    private Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtUtil.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
            return null;
        }
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(String username, Claims claims,
            HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(username, null, extractAuthorities(claims));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }

    private boolean isUserRevoked(String email) {
        if (userRepositoryPort == null) {
            return false;
        }
        return userRepositoryPort.findByEmailIncludingDeleted(email)
                .map(User::isDeleted)
                .orElse(true);
    }

    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(ERROR_BODY);
    }

    @SuppressWarnings("unchecked")
    private List<GrantedAuthority> extractAuthorities(Claims claims) {
        Object raw = claims.get(CLAIM_AUTHORITIES);
        if (raw instanceof Collection<?> collection) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            for (Object entry : collection) {
                authorities.add(mapAuthority(entry));
            }
            return authorities;
        }
        return Collections.emptyList();
    }

    private GrantedAuthority mapAuthority(Object entry) {
        if (entry instanceof Map<?, ?> map) {
            Object authority = map.get("authority");
            if (authority == null) {
                authority = map.get("role");
            }
            if (authority != null) {
                return new SimpleGrantedAuthority(authority.toString());
            }
        }
        return new SimpleGrantedAuthority(entry.toString());
    }
}
