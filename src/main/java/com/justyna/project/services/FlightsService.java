package com.justyna.project.services;

import com.justyna.project.model.relational.Airport;
import com.justyna.project.model.relational.City;
import com.justyna.project.model.relational.Flight;
import com.justyna.project.model.relational.FlightLeg;
import com.justyna.project.repositories.graph.AirportGraphRepository;
import com.justyna.project.repositories.graph.FlightGraphRepository;
import com.justyna.project.repositories.relational.AirportRelRepository;
import com.justyna.project.repositories.relational.FlightLegRelRepository;
import org.neo4j.driver.internal.InternalPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FlightsService {

    private final AirportRelRepository airportRelRepository;
    private final AirportGraphRepository airportRepositoryG;
    private final FlightLegRelRepository flightRelRepository;
    private final FlightGraphRepository flightGraphRepository;
    private final AlgorithmSearcherService algorithmSearcherService;

    @Autowired
    public FlightsService(AirportRelRepository airportRelRepository, AirportGraphRepository airportRepositoryG, FlightLegRelRepository flightRelRepository, FlightGraphRepository flightGraphRepository, AlgorithmSearcherService algorithmSearcherService) {
        this.airportRelRepository = airportRelRepository;
        this.airportRepositoryG = airportRepositoryG;
        this.flightRelRepository = flightRelRepository;
        this.flightGraphRepository = flightGraphRepository;
        this.algorithmSearcherService = algorithmSearcherService;
    }

    public void translateAirports(Set<Airport> airportsToPutInDB,
                                  Set<FlightLeg> flightsToPutInDB) {
        Iterable<Airport> airports =
                airportRelRepository.saveAll(airportsToPutInDB);

        airports.forEach(airport ->
                airportRepositoryG.save(new com.justyna.project.model.graph.Airport(airport.getName(), airport.getCode(),
                        airport.getLatitude(), airport.getLongitude()))
        );

        Iterable<FlightLeg> flights = flightRelRepository.saveAll(flightsToPutInDB);

        flights.forEach(flightR -> {
            com.justyna.project.model.graph.Airport a1 = airportRepositoryG.findAirportByCode(flightR.getDepartureAirport().getCode());
            com.justyna.project.model.graph.Airport a2 = airportRepositoryG.findAirportByCode(flightR.getArrivalAirport().getCode());

            com.justyna.project.model.graph.Flight flightG = a1.connectsWith(a2);
            flightG.setCode(flightR.getId());

            airportRepositoryG.save(a1);
            airportRepositoryG.save(a2);
        });
    }

    public List<Flight> getOptimalFlightsByCities(City departCity, City arrivalCity) {
        List<Flight> flights = new ArrayList<>();
        departCity.getAirport().forEach(airportD ->
                arrivalCity.getAirport().forEach(airportA -> flights.addAll(getOptimalFlightsByAirports(airportD, airportA))));
        return flights;
    }

    public List<Flight> getOptimalFlightsByAirports(Airport departAirport, Airport arrivalAirport) {
        return algorithmSearcherService.getFlights(getFlightsFromDB(departAirport, arrivalAirport));
    }


    private List<Flight> getFlightsFromDB(Airport departAirport, Airport arrivalAirport) {
        List<Flight> flights = new ArrayList<>();
        Iterable<Map<String, InternalPath>> results =
                flightGraphRepository.findTheShortest(departAirport.getCode(), arrivalAirport.getCode());

        for (Map<String, InternalPath> row : results) {
            Flight onePath = new Flight();
            row.get("p").forEach(r ->
                    onePath.getFlightLegs().add(flightRelRepository.findById(r.relationship().get("code").asLong()).orElse(new FlightLeg())));
            flights.add(onePath);
        }

        return flights;
    }
}
