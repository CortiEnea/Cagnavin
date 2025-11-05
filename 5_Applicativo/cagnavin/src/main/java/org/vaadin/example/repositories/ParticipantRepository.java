package org.vaadin.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.example.entities.Participant;
import org.vaadin.example.entities.Trip;
import org.vaadin.example.entities.Users;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAll();

    List<Participant> findByTrip(Trip trip);

    List<Participant> findByUsername(Users user);
}