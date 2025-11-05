package org.vaadin.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.example.entities.Users;
import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long> {

    List<Users> findAll();

    Users findByUsername(String username);

}