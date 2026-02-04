
package com.example.demo.adapters.in.api.security;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import com.example.demo.adapters.out.persistence.jpa.entities.UserEntity;
import com.example.demo.adapters.out.persistence.jpa.interfaces.UserRepositoryJpa;



import io.jsonwebtoken.lang.Collections;

@Service

public class JpaUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepositoryJpa repositoryJpa;

@Override
@Transactional(readOnly = true)
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity u = repositoryJpa.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

    // Si tus roles NO tienen prefijo ROLE_ y usás hasRole(), añadí el prefijo aquí
    List<GrantedAuthority> authorities = Optional.ofNullable(u.getRoles())
        .orElse(Collections.emptyList())
        .stream()
        .map(role -> {
            String roleName = role.getName();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName; // opcional: depende de tu convención
            }
            return new SimpleGrantedAuthority(roleName);
        })
        .collect(Collectors.toList());

    boolean enabled = u.getDeletedAt() == null;

    return new org.springframework.security.core.userdetails.User(
        u.getEmail(),
        u.getPassword(),
        enabled,
        true, 
        true, 
        true,
        authorities
    );
}


}
