package com.justyna.project.services.general;

import com.justyna.project.model.graph.Airport;
import com.justyna.project.model.relational.Flight;
import com.justyna.project.model.relational.FlightLeg;
import com.justyna.project.repositories.graph.AirportGraphRepository;
import com.justyna.project.repositories.graph.FlightGraphRepository;
import com.justyna.project.repositories.relational.AirportRelRepository;
import com.justyna.project.repositories.relational.FlightRelRepository;
import org.neo4j.driver.internal.InternalPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FlightsService {

    private final AirportRelRepository airportRelRepository;
    private final AirportGraphRepository airportRepositoryG;
    private final FlightRelRepository flightRelRepository;
    private final FlightGraphRepository flightGraphRepository;

    @Autowired
    public FlightsService(AirportRelRepository airportRelRepository, AirportGraphRepository airportRepositoryG, FlightRelRepository flightRelRepository, FlightGraphRepository flightGraphRepository) {
        this.airportRelRepository = airportRelRepository;
        this.airportRepositoryG = airportRepositoryG;
        this.flightRelRepository = flightRelRepository;
        this.flightGraphRepository = flightGraphRepository;
    }

    public void translateAirports(Set<com.justyna.project.model.relational.Airport> airportsToPutInDB,
                                  Set<FlightLeg> flightsToPutInDB) {
        Iterable<com.justyna.project.model.relational.Airport> airports =
                airportRelRepository.saveAll(airportsToPutInDB);

        airports.forEach(airport ->
                airportRepositoryG.save(new Airport(airport.getName(), airport.getCode(),
                        airport.getLatitude(), airport.getLongitude()))
        );

        Iterable<FlightLeg> flights = flightRelRepository.saveAll(flightsToPutInDB);

        flights.forEach(flightR -> {
            Airport a1 = airportRepositoryG.findAirportByCode(flightR.getDepartureAirport().getCode());
            Airport a2 = airportRepositoryG.findAirportByCode(flightR.getArrivalAirport().getCode());

            com.justyna.project.model.graph.Flight flightG = a1.connectsWith(a2);
            flightG.setCode(flightR.getId());

            airportRepositoryG.save(a1);
            airportRepositoryG.save(a2);
        });
    }

    public List<Flight> sortByTimeOfFlight(com.justyna.project.model.relational.Airport departAirport,
                                           com.justyna.project.model.relational.Airport arrivalAirport) {

        List<Flight> flights = getWithoutRedundance(departAirport, arrivalAirport);
        flights.sort(this::compare);

        return flights;
    }


    private List<Flight> getFlights(com.justyna.project.model.relational.Airport departAirport,
                                    com.justyna.project.model.relational.Airport arrivalAirport) {
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


    private List<Flight> findTheAvailableByTime(com.justyna.project.model.relational.Airport departAirport,
                                                com.justyna.project.model.relational.Airport arrivalAirport) {

        List<Flight> candidates = getFlights(departAirport, arrivalAirport);
        List<Flight> proposition = new ArrayList<>();

        for (Flight onePath : candidates) {
            List<FlightLeg> flightLegs = onePath.getFlightLegs();
            boolean flag = false;
            for (int i = 1; i < flightLegs.size(); i++) {
                ZonedDateTime departTime = ZonedDateTime.parse(flightLegs.get(i).getDepartureTimeUTC());
                ZonedDateTime arrivalTime = ZonedDateTime.parse(flightLegs.get(i - 1).getArrivalTimeUTC());
                if (!departTime.isAfter(arrivalTime)) {
                    flag = false;
                    break;
                } else flag = true;
            }
            if (flag) proposition.add(onePath);
        }
        return proposition;
    }


    private List<Flight> getWithoutRedundance(com.justyna.project.model.relational.Airport departAirport,
                                              com.justyna.project.model.relational.Airport arrivalAirport) {

        List<Flight> candidates = findTheAvailableByTime(departAirport, arrivalAirport);
        List<Flight> proposition = new ArrayList<>();

        for (Flight flight : candidates) {
            List<FlightLeg> flightLegs = flight.getFlightLegs();
            boolean flag = false;
            for (int i = 0; i < flightLegs.size(); i++) {
                for (int j = 0; j < flightLegs.size(); j++) {
                    if (i != j && (flightLegs.get(i).getDepartureAirport().getCode().equals(flightLegs.get(j).getArrivalAirport().getCode()) && i < j)) {
                        flag = false;
                        break;
                    } else flag = true;
                }
                if (!flag) break;
            }
            if (flag) proposition.add(flight);
        }

        return proposition;
    }


    private int compare(Flight flight1, Flight flight2) {
        ZonedDateTime flight1start = ZonedDateTime.parse(flight1.getFlightLegs().get(0).getDepartureTimeUTC());
        ZonedDateTime flight1stop = ZonedDateTime.parse(flight1.getFlightLegs().get(flight1.getFlightLegs().size() - 1).getArrivalTimeUTC());
        ZonedDateTime flight2start = ZonedDateTime.parse(flight2.getFlightLegs().get(0).getDepartureTimeUTC());
        ZonedDateTime flight2stop = ZonedDateTime.parse(flight2.getFlightLegs().get(flight2.getFlightLegs().size() - 1).getArrivalTimeUTC());

        return Long.compare(Duration.between(flight1start, flight1stop).toMinutes(), Duration.between(flight2start, flight2stop).toMinutes());
    }
}
