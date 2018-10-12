package com.justyna.project.controllers;

import com.justyna.project.model.other.PassengerDetail;
import com.justyna.project.model.relational.Passenger;
import com.justyna.project.model.relational.Place;
import com.justyna.project.model.relational.Reservation;
import com.justyna.project.repositories.relational.FlightLegRelRepository;
import com.justyna.project.repositories.relational.PassengerRepository;
import com.justyna.project.repositories.relational.PlaceRepository;
import com.justyna.project.repositories.relational.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private final PassengerRepository passengerRepository;
    private final FlightLegRelRepository flightLegRelRepository;
    private final PlaceRepository placeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationController(PassengerRepository passengerRepository, FlightLegRelRepository flightLegRelRepository,
                                 PlaceRepository placeRepository, ReservationRepository reservationRepository) {
        this.passengerRepository = passengerRepository;
        this.flightLegRelRepository = flightLegRelRepository;
        this.placeRepository = placeRepository;
        this.reservationRepository = reservationRepository;
    }

    @CrossOrigin
    @RequestMapping(value = "/{flightLegId}", method = RequestMethod.POST)
    public ResponseEntity<Iterable<Reservation>> addReservation(@PathVariable Long flightLegId, @RequestBody List<PassengerDetail> passengerDetails) {
        List<Reservation> flightLegDetailsList = new ArrayList<>();
        for (PassengerDetail passengerDetail : passengerDetails) {
            Passenger passenger = passengerDetail.getPassenger();
            passengerRepository.save(passenger);
            flightLegDetailsList.add(new Reservation(passenger, placeRepository.findById(passengerDetail.getPlaceId()).get(),
                    flightLegRelRepository.findById(flightLegId).get()));
        }

        return new ResponseEntity<>(reservationRepository.saveAll(flightLegDetailsList), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "places/{flightLegId}", method = RequestMethod.GET)
    public ResponseEntity<Iterable<Place>> getPlacesOnSpecificFlightLeg(@PathVariable Long flightLegId) {
        List<Place> places = reservationRepository.findPlacesByFlightLegId(flightLegId);
        return new ResponseEntity<>(places, HttpStatus.OK);
    }
}
