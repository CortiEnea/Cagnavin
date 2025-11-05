package org.vaadin.example.services;

import org.springframework.stereotype.Service;
import org.vaadin.example.entities.Request;
import org.vaadin.example.entities.Trip;
import org.vaadin.example.entities.Users;
import org.vaadin.example.repositories.RequestRepository;

import java.util.List;

@Service
public class RequestService {
    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<Request> all() {
        return requestRepository.findAll();
    }

    public void delete(Request request) {
        requestRepository.delete(request);
    }

    public List<Request> findByTrip(Trip trip) {
        return requestRepository.findByTrip(trip);
    }

    public List<Request> findByUser(Users user) {
        return requestRepository.findByUsername(user);
    }


    public Request addRequest(Request newRequest) {
        return requestRepository.save(newRequest);
    }
}
