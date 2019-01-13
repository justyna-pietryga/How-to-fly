package com.justyna.project.controllers;

import com.justyna.project.model.relational.City;
import com.justyna.project.repositories.relational.CityRelRepository;
import com.justyna.project.repositories.relational.CountryRelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityRelRepository cityRelRepository;
    private final CountryRelRepository countryRelRepository;

    @Autowired
    public CityController(CityRelRepository cityRelRepository, CountryRelRepository countryRelRepository) {
        this.cityRelRepository = cityRelRepository;
        this.countryRelRepository = countryRelRepository;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public Iterable<City> getAllCities() {
        return cityRelRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<City> getCityById(@PathVariable Long id) {
        Optional<City> city = cityRelRepository.findById(id);
        return city.map(city1 -> new ResponseEntity<>(city1, HttpStatus.OK)).orElseGet(
                () -> new ResponseEntity<>((City) null, HttpStatus.BAD_REQUEST));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<City> addCity(@RequestBody City city) {
        Optional<City> city1 = cityRelRepository.findByName(city.getName());
        if (city1.isPresent()) return new ResponseEntity<>(cityRelRepository.save(city), HttpStatus.OK);
        else return new ResponseEntity<>((City) null, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<City> updateCity(@RequestBody City city, @PathVariable Long id) {

        return cityRelRepository.findById(id)
                .map(city1 -> {
                    city1.setName(city.getName());
                    city1.setLatitude(city.getLatitude());
                    city1.setLongitude(city.getLongitude());
                    if (city.getCountry() != null & countryRelRepository.findById(city.getCountry().getId()).isPresent())
                        city1.setCountry(city.getCountry());
                    return new ResponseEntity<>(cityRelRepository.save(city1), HttpStatus.OK);
                })
                .orElseGet(() -> {
                    city.setId(id);
                    return new ResponseEntity<>(cityRelRepository.save(city), HttpStatus.OK);
                });
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteCity(@PathVariable long id) {
        cityRelRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
