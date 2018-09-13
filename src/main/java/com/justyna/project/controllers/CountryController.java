package com.justyna.project.controllers;

import com.justyna.project.model.relational.Country;
import com.justyna.project.repositories.relational.CountryRelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/countries")
public class CountryController {
    private final CountryRelRepository countryRelRepository;

    @Autowired
    public CountryController(CountryRelRepository countryRelRepository) {
        this.countryRelRepository = countryRelRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Country> getAllCountries() {
        return countryRelRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Country> getCountryById(@PathVariable Long id) {
        Optional<Country> country = countryRelRepository.findById(id);
        return country.map(country1 -> new ResponseEntity<>(country1, HttpStatus.OK)).orElseGet(
                () -> new ResponseEntity<>((Country) null, HttpStatus.BAD_REQUEST));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Country> addCountry(@RequestBody Country country) {
        return new ResponseEntity<>(countryRelRepository.save(country), HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Country> updateCity(@RequestBody Country country, @PathVariable Long id) {

        return countryRelRepository.findById(id)
                .map(country1 -> {
                    country1.setName(country.getName());
                    return new ResponseEntity<>(countryRelRepository.save(country1), HttpStatus.OK);
                })
                .orElseGet(() -> {
                    country.setId(id);
                    return new ResponseEntity<>(countryRelRepository.save(country), HttpStatus.OK);
                });
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteCountry(@PathVariable long id) {
        countryRelRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
