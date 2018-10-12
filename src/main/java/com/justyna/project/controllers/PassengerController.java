package com.justyna.project.controllers;

import com.justyna.project.model.relational.Passenger;
import com.justyna.project.repositories.relational.AddressRepository;
import com.justyna.project.repositories.relational.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    private final PassengerRepository passengerRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public PassengerController(PassengerRepository passengerRepository, AddressRepository addressRepository) {
        this.passengerRepository = passengerRepository;
        this.addressRepository = addressRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Passenger> getPassengerById(@PathVariable Long id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        return passenger.map(passenger1 -> new ResponseEntity<>(passenger1, HttpStatus.OK)).orElseGet(
                () -> new ResponseEntity<>((Passenger) null, HttpStatus.BAD_REQUEST));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Passenger> addPassenger(@RequestBody Passenger passenger) {
        return new ResponseEntity<>(passengerRepository.save(passenger), HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Passenger> updatePassenger(@RequestBody Passenger passenger, @PathVariable Long id) {

        return passengerRepository.findById(id)
                .map(passenger1 -> {
                    passenger1.setName(passenger.getName());
                    passenger1.setSurname(passenger.getSurname());
                    passenger1.setPesel(passenger.getPesel());
                    passenger1.setTelephone(passenger.getTelephone());
//                    if (passenger.getAddress() != null & addressRepository.findById(passenger.getAddress().getId()).isPresent())
//                        passenger1.setAddress(passenger.getAddress());
                    return new ResponseEntity<>(passengerRepository.save(passenger1), HttpStatus.OK);
                })
                .orElseGet(() -> {
                    passenger.setId(id);
                    return new ResponseEntity<>(passengerRepository.save(passenger), HttpStatus.OK);
                });
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deletePassenger(@PathVariable long id) {
        passengerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
