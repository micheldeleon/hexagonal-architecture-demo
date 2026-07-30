package com.tutorneo.adapters.in.api.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String HEADER_AUTH = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HEADER_AUTH);

        if (header == null || !header.startsWith(PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(PREFIX.length());

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtUtil.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = extractAuthorities(claims);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Token inválido o expirado\"}");
        }
    }

    private List<GrantedAuthority> extractAuthorities(Claims claims) {
        Object raw = claims.get("authorities");
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
        if (entry instanceof Map<?, ?> map && map.get("authority") != null) {
            return new SimpleGrantedAuthority(map.get("authority").toString());
        }
        return new SimpleGrantedAuthority(entry.toString());
    }
}
