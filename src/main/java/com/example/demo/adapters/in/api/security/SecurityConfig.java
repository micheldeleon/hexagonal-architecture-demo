package com.example.demo.adapters.in.api.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.example.demo.core.ports.out.UserRepositoryPort;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil, UserRepositoryPort userRepositoryPort)
            throws Exception {
        return http.authorizeHttpRequests((authz) -> authz
                // OPTIONS para CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Login
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/login/google").permitAll()
                
                // ===== ADMIN =====
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // ===== USUARIOS - Reglas específicas primero =====
                .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/users/profile").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/organizer").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/users/organizer-requests").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/users/organizer-requests/me").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/users/{id}/profile-image").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/users/by-id-and-email").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/tournaments").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                
                // ===== TORNEOS - Reglas específicas primero =====
                // Imágenes (específico antes que genérico)
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{id}/image").authenticated()
                
                // Fixture
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{id}/fixture/elimination").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{id}/fixture/league").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.GET, "/api/tournaments/{id}/fixture").permitAll()
                
                // Resultados de carreras
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{id}/race/results").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.GET, "/api/tournaments/{id}/race/results").permitAll()
                
                // Resultados de partidos
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{tournamentId}/matches/{matchId}/league-result").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{tournamentId}/matches/{matchId}/result").hasRole("ORGANIZER")
                
                // Inscripciones
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{id}/register").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{id}/register/runner").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{id}/register/team").permitAll()
                
                // Remover equipo
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{tournamentId}/remove-team").permitAll()
                
                // Acciones del organizador
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{id}/cancel").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/tournaments/{id}/start").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.PUT, "/api/tournaments/{id}").authenticated()
                
                // Standings
                .requestMatchers(HttpMethod.GET, "/api/tournaments/{id}/standings").permitAll()
                
                // Consultas públicas de torneos
                .requestMatchers(HttpMethod.GET, "/api/tournaments/public").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments/status").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments/all").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments/latest").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments").permitAll()
                
                // ===== DISCIPLINAS =====
                .requestMatchers(HttpMethod.GET, "/api/disciplines/**").permitAll()
                
                // ===== NOTIFICACIONES =====
                .requestMatchers(HttpMethod.POST, "/api/notifications/create").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/notifications/stream").authenticated()
                
                // ===== REPUTACIA"N =====
                .requestMatchers(HttpMethod.POST, "/api/organizers/{id}/rate").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/organizers/{id}/reputation").permitAll()
                
                // ===== MAIL (Testing) =====
                .requestMatchers(HttpMethod.POST, "/api/mail/test").permitAll()
                
                // Health checks
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .requestMatchers(HttpMethod.GET, "/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/info").permitAll()
                .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtUtil, userRepositoryPort))
                .addFilterBefore(new JwtValidationFilter(authenticationManager(), jwtUtil, userRepositoryPort),
                        UsernamePasswordAuthenticationFilter.class)
                .csrf(config -> config.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Cambiá esto por la URL real de tu frontend
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toList());
        if (origins.isEmpty()) {
            origins = List.of("http://localhost:5173");
        }
        config.setAllowedOrigins(origins);

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(
                new CorsFilter(corsConfigurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }

}
