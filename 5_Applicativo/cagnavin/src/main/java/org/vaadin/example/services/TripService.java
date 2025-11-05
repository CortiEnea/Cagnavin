package org.vaadin.example.services;

import org.springframework.stereotype.Service;
import org.vaadin.example.entities.Trip;
import org.vaadin.example.repositories.TripRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TripService {
    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<Trip> all() {
        return tripRepository.findAll();
    }

    public Optional<Trip> findById(Long id) {
        return tripRepository.findById(id);
    }

    public List<Trip> giteFuture() {
        LocalDate now = LocalDate.now();
        return tripRepository.findByDataAfter(now);
    }

    public void delete(Trip trip) {
        tripRepository.delete(trip);
    }

    public List<Trip> gitePassate() {
        LocalDate now = LocalDate.now();
        return tripRepository.findByDataBefore(now);
    }

    public Trip addTrip(Trip newTrip) {
        return tripRepository.save(newTrip);
    }
}
