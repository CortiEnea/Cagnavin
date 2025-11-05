package org.vaadin.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.example.entities.Request;
import org.vaadin.example.entities.Trip;
import org.vaadin.example.entities.Users;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAll();

    List<Request> findByTrip(Trip trip);

    List<Request> findByUsername(Users user);
}