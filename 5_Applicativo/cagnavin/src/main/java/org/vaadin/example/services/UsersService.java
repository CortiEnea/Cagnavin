package org.vaadin.example.services;

import org.springframework.stereotype.Service;
import org.vaadin.example.entities.Users;
import org.vaadin.example.repositories.UsersRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<Users> all() {
        return usersRepository.findAll();
    }

    public void delete(Users user) {
        usersRepository.delete(user);
    }

    public Users aggiungiUtente(Users nuovoUtente) {
        return usersRepository.save(nuovoUtente);
    }

    public Optional<Users> findById(Long id) {
        return usersRepository.findById(id);
    }

    public Users findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

}
