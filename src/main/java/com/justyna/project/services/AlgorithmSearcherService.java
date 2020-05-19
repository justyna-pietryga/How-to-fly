package com.justyna.project.services;

import com.justyna.project.model.relational.Flight;
import com.justyna.project.model.relational.FlightLeg;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlgorithmSearcherService {

    public List<Flight> getFlights(List<Flight> allFlights) {
        return new FlightSearcher(allFlights)
                .findTheAvailableByTime(30)
                .getWithoutRedundance()
                .sortByTimeOfFlight()
                .getFlights();
    }
}
