package com.justyna.project.controllers;

import com.justyna.project.model.relational.Place;
import com.justyna.project.repositories.relational.PlaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceRepository placeRepository;

    public PlaceController(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
        Optional<Place> place = placeRepository.findById(id);
        return place.map(place1 -> new ResponseEntity<>(place1, HttpStatus.OK)).orElseGet(
                () -> new ResponseEntity<>((Place) null, HttpStatus.BAD_REQUEST));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Place> addPlace(@RequestBody Place place) {
        return new ResponseEntity<>(placeRepository.save(place), HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Place> updatePlace(@RequestBody Place place, @PathVariable Long id) {

        return placeRepository.findById(id)
                .map(place1 -> {
                    place1.setCode(place.getCode());
                    place1.setCabinClass(place.getCabinClass());
                    if (place.getAirplane() != null & placeRepository.findById(place.getAirplane().getId()).isPresent())
                        place1.setAirplane(place.getAirplane());
                    return new ResponseEntity<>(placeRepository.save(place1), HttpStatus.OK);
                })
                .orElseGet(() -> {
                    place.setId(id);
                    return new ResponseEntity<>(placeRepository.save(place), HttpStatus.OK);
                });
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deletePlace(@PathVariable long id) {
        placeRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
