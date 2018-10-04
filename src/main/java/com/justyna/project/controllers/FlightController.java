package com.justyna.project.controllers;

import com.justyna.project.model.relational.Airport;
import com.justyna.project.model.relational.City;
import com.justyna.project.model.relational.Flight;
import com.justyna.project.repositories.relational.AirportRelRepository;
import com.justyna.project.repositories.relational.CityRelRepository;
import com.justyna.project.services.FlightsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightsService flightsService;
    private final AirportRelRepository airportRelRepository;
    private final CityRelRepository cityRelRepository;

    public FlightController(FlightsService flightsService, AirportRelRepository airportRelRepository, CityRelRepository cityRelRepository) {
        this.flightsService = flightsService;
        this.airportRelRepository = airportRelRepository;
        this.cityRelRepository = cityRelRepository;
    }

    @RequestMapping(path = "/search2/departureId={departureAirportId},arrivalId={arrivalAirportId}", method = RequestMethod.GET)
    public ResponseEntity<List<Flight>> search2(@PathVariable Long departureAirportId, @PathVariable Long arrivalAirportId) {
        Optional<Airport> departure = airportRelRepository.findById(departureAirportId);
        Optional<Airport> arrival = airportRelRepository.findById(arrivalAirportId);
        if (departure.isPresent() && arrival.isPresent()) {
            return new ResponseEntity<>(
                    flightsService.getOptimalFlightsByAirports(departure.get(), arrival.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>((List<Flight>) null, HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin
    @RequestMapping(path = "/search/departureId={departureCitytId},arrivalId={arrivalCityId}", method = RequestMethod.GET)
    public ResponseEntity<List<Flight>> search(@PathVariable Long departureCitytId, @PathVariable Long arrivalCityId) {
        Optional<City> departure = cityRelRepository.findById(departureCitytId);
        Optional<City> arrival = cityRelRepository.findById(arrivalCityId);
        if (departure.isPresent() && arrival.isPresent()) {
            return new ResponseEntity<>(
                    flightsService.getOptimalFlightsByCities(departure.get(), arrival.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>((List<Flight>) null, HttpStatus.BAD_REQUEST);
    }
}
