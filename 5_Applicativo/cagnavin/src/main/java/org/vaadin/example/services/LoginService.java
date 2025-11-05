package org.vaadin.example.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.vaadin.example.entities.Users;
import org.vaadin.example.security.CustomUserDetails;

@Service
public class LoginService {

    private final UsersService usersService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginService(UsersService usersService, BCryptPasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String username, String password) {
        Users utente = usersService.findByUsername(username);
        return utente != null && passwordEncoder.matches(password, utente.getPassword());
    }

    public UserDetails loadUserByUsername(String username) {
        Users utente = usersService.findByUsername(username);
        if (utente == null) {
            throw new UsernameNotFoundException("Utente non trovato: " + username);
        }
        return new CustomUserDetails(utente);
    }
}
