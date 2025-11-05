package org.vaadin.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.example.entities.Trip;

import java.time.LocalDate;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findAll();

    List<Trip> findByDataAfter(LocalDate data);

    List<Trip> findByDataBefore(LocalDate data);
}