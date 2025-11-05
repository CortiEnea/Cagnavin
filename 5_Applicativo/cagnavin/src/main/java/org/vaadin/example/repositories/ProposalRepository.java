package org.vaadin.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.example.entities.Proposal;
import org.vaadin.example.entities.Trip;
import org.vaadin.example.entities.Users;

import java.time.LocalDate;
import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    List<Proposal> findAll();
    List<Proposal> findByUsername(Users user);

}