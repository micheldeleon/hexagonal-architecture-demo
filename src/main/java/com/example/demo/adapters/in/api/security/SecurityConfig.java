package com.example.demo.adapters.in.api.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()// sacar
                .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/users/profile").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/by-id-and-email").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/disciplines/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments/public").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments/status").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tournaments/*/register").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/tournaments/*/register/runner").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/tournaments/*/register/team").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tournaments/*/fixture/elimination").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.POST, "/api/tournaments/*/fixture/league").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.GET, "/api/tournaments/*/fixture").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tournaments/*/race/results").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.GET, "/api/tournaments/*/race/results").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tournaments/*/matches/*/league-result").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.POST, "/api/tournaments/*/matches/*/result").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.GET, "/api/tournaments/*/standings").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/mail/test").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/organizer").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/tournaments/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/tournaments?**").permitAll()
                .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtUtil, userRepositoryPort))
                .addFilterBefore(new JwtValidationFilter(authenticationManager(), jwtUtil),
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
        config.setAllowedOrigins(List.of("http://localhost:5173"));

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
