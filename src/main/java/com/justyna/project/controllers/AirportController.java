package com.justyna.project.controllers;

import com.justyna.project.model.relational.Airport;
import com.justyna.project.repositories.relational.AirportRelRepository;
import com.justyna.project.repositories.relational.CityRelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportRelRepository airportRelRepository;
    private final CityRelRepository cityRelRepository;

    public AirportController(AirportRelRepository airportRelRepository, CityRelRepository cityRelRepository) {
        this.airportRelRepository = airportRelRepository;
        this.cityRelRepository = cityRelRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Airport> getAllAirports() {
        return airportRelRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Airport> getAirportById(@PathVariable Long id) {
        Optional<Airport> airport = airportRelRepository.findById(id);
        return airport.map(airport1 -> new ResponseEntity<>(airport1, HttpStatus.OK)).orElseGet(
                () -> new ResponseEntity<>((Airport) null, HttpStatus.BAD_REQUEST));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Airport> addAirport(@RequestBody Airport airport) {
        return new ResponseEntity<>(airportRelRepository.save(airport), HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Airport> updateAirport(@RequestBody Airport airport, @PathVariable Long id) {

        return airportRelRepository.findById(id)
                .map(airport1 -> {
                    airport1.setName(airport.getName());
                    airport1.setCode(airport.getCode());
                    airport1.setLatitude(airport.getLatitude());
                    airport1.setLongitude(airport.getLongitude());
                    if (airport.getCity() != null & cityRelRepository.findById(airport.getCity().getId()).isPresent())
                        airport1.setCity(airport.getCity());
                    return new ResponseEntity<>(airportRelRepository.save(airport1), HttpStatus.OK);
                })
                .orElseGet(() -> {
                    airport.setId(id);
                    return new ResponseEntity<>(airportRelRepository.save(airport), HttpStatus.OK);
                });
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAirport(@PathVariable long id) {
        airportRelRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
