package org.vaadin.example.services;

import org.springframework.stereotype.Service;
import org.vaadin.example.entities.Proposal;
import org.vaadin.example.entities.Request;
import org.vaadin.example.entities.Trip;
import org.vaadin.example.entities.Users;
import org.vaadin.example.repositories.ProposalRepository;
import org.vaadin.example.repositories.TripRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProposalService {
    private final ProposalRepository proposalRepository;

    public ProposalService(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    public List<Proposal> all() {
        return proposalRepository.findAll();
    }

    public void delete(Proposal proposal){
        proposalRepository.delete(proposal);
    }

    public Proposal addProposal(Proposal newProposal) {
        return proposalRepository.save(newProposal);
    }

    public List<Proposal> findByUser(Users user) {
        return proposalRepository.findByUsername(user);
    }
}
