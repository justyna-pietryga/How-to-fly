package com.justyna.project.controllers;

import com.justyna.project.model.relational.Pnr;
import com.justyna.project.repositories.relational.PnrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/pnrs")
public class PnrController {

    private final PnrRepository pnrRepository;

    @Autowired
    public PnrController(PnrRepository pnrRepository) {
        this.pnrRepository = pnrRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Pnr> getAllPnrs() {
        return pnrRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Pnr> getPnrById(@PathVariable Long id) {
        Optional<Pnr> pnr = pnrRepository.findById(id);
        return pnr.map(pnr1 -> new ResponseEntity<>(pnr1, HttpStatus.OK)).orElseGet(
                () -> new ResponseEntity<>((Pnr) null, HttpStatus.BAD_REQUEST));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Pnr> addPnr(@RequestBody Pnr pnr) {
        return new ResponseEntity<>(pnrRepository.save(pnr), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deletePnr(@PathVariable long id) {
        pnrRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
