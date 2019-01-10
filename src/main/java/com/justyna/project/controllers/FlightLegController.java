package com.justyna.project.controllers;

import com.justyna.project.model.other.FlightLegDto;
import com.justyna.project.model.relational.Airport;
import com.justyna.project.model.relational.FlightLeg;
import com.justyna.project.repositories.relational.AirportRelRepository;
import com.justyna.project.repositories.relational.FlightLegRelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/flight-legs")
public class FlightLegController {

    private final FlightLegRelRepository flightLegRelRepository;
    private final AirportRelRepository airportRelRepository;

    @Autowired
    public FlightLegController(FlightLegRelRepository flightLegRelRepository, AirportRelRepository airportRelRepository) {
        this.flightLegRelRepository = flightLegRelRepository;
        this.airportRelRepository = airportRelRepository;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public Iterable<FlightLeg> getAllFlightLegs() {
        return flightLegRelRepository.findAll();
    }

    @CrossOrigin
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<FlightLeg> getFlightLegById(@PathVariable Long id) {
        Optional<FlightLeg> flightLeg = flightLegRelRepository.findById(id);
        return flightLeg.map(flightLeg1 -> new ResponseEntity<>(flightLeg1, HttpStatus.OK)).orElseGet(
                () -> new ResponseEntity<>((FlightLeg) null, HttpStatus.BAD_REQUEST));
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<FlightLeg> addFlightLeg(@RequestBody FlightLegDto flightLegDto) {
        FlightLeg flightLeg = new FlightLeg();
        Airport departure = flightLegDto.getDepartureAirport();
        Airport arrival = flightLegDto.getArrivalAirport();
        if (airportRelRepository.findById(departure.getId()).isPresent()) flightLeg.setDepartureAirport(departure);
        if (airportRelRepository.findById(departure.getId()).isPresent()) flightLeg.setArrivalAirport(arrival);
        flightLeg.setDepartureTime(flightLegDto.getDepartureTime(), flightLegDto.getTimeMode());
        flightLeg.setArrivalTime(flightLegDto.getArrivalTime(), flightLegDto.getTimeMode());
        flightLeg.setAirplane(flightLegDto.getAirplane());

        return new ResponseEntity<>(flightLegRelRepository.save(flightLeg), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<FlightLeg> updateFlightLeg(@RequestBody FlightLegDto flightLegDto, @PathVariable Long id) {

        return flightLegRelRepository.findById(id).map(flightLeg1 -> setParametersToUpdate(flightLegDto, flightLeg1))
                .orElseGet(() -> setParametersToUpdate(flightLegDto, new FlightLeg(id)));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteFlightLeg(@PathVariable long id) {
        flightLegRelRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<FlightLeg> setParametersToUpdate(FlightLegDto flightLegDto, FlightLeg flightLeg) {
        if (flightLegDto.getArrivalAirport() != null) {
            Optional<Airport> arrival = airportRelRepository.findById(flightLegDto.getArrivalAirport().getId());
            arrival.ifPresent(flightLeg::setArrivalAirport);
        }

        if (flightLegDto.getDepartureAirport() != null) {
            Optional<Airport> departure = airportRelRepository.findById(flightLegDto.getDepartureAirport().getId());
            departure.ifPresent(flightLeg::setDepartureAirport);
        }

        if (flightLegDto.getDepartureTime() != null) {
            flightLeg.setDepartureTime(flightLegDto.getDepartureTime(), flightLegDto.getTimeMode());
        }
        if (flightLegDto.getArrivalAirport() != null) {
            flightLeg.setArrivalTime(flightLegDto.getArrivalTime(), flightLegDto.getTimeMode());
        }

        if (flightLegDto.getAirplane() != null) {
            flightLeg.setAirplane(flightLegDto.getAirplane());
        }

        return new ResponseEntity<>(flightLegRelRepository.save(flightLeg), HttpStatus.OK);
    }
}
