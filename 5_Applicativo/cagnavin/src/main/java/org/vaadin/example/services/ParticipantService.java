package org.vaadin.example.services;

import org.springframework.stereotype.Service;
import org.vaadin.example.entities.Participant;
import org.vaadin.example.entities.Trip;
import org.vaadin.example.entities.Users;
import org.vaadin.example.repositories.ParticipantRepository;

import java.util.List;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public List<Participant> all() {
        return participantRepository.findAll();
    }

    public List<Participant> findByTrip(Trip trip) {
        return participantRepository.findByTrip(trip);
    }

    public Participant addParticipant(Participant newParticipant) {
        return participantRepository.save(newParticipant);
    }

    public void delete(Participant participant) {
        participantRepository.delete(participant);
    }

    public List<Participant> findByUser(Users user) {
        return participantRepository.findByUsername(user);
    }
}
