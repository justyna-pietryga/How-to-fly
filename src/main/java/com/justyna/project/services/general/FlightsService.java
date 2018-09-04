package com.justyna.project.services.general;

import com.justyna.project.model.graph.Airport;
import com.justyna.project.model.relational.Flight;
import com.justyna.project.repositories.graph.AirportGraphRepository;
import com.justyna.project.repositories.graph.FlightGraphRepository;
import com.justyna.project.repositories.relational.AirportRelRepository;
import com.justyna.project.repositories.relational.FlightRelRepository;
import org.neo4j.driver.internal.InternalPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FlightsService {
    @Autowired
    private AirportRelRepository airportRelRepository;
    @Autowired
    private AirportGraphRepository airportRepositoryG;
    @Autowired
    private FlightRelRepository flightRelRepository;

    @Autowired
    private FlightGraphRepository flightGraphRepository;

    public void translateAirports(Set<com.justyna.project.model.relational.Airport> airportsToPutInDB,
                                  Set<Flight> flightsToPutInDB) {
        Iterable<com.justyna.project.model.relational.Airport> airports =
                airportRelRepository.saveAll(airportsToPutInDB);

        airports.forEach(airport ->
                airportRepositoryG.save(new Airport(airport.getName(), airport.getCode(),
                        airport.getLatitude(), airport.getLongitude()))
        );

        Iterable<Flight> flights = flightRelRepository.saveAll(flightsToPutInDB);

        flights.forEach(flightR -> {
            Airport a1 = airportRepositoryG.findAirportByCode(flightR.getDepartureAirport().getCode());
            Airport a2 = airportRepositoryG.findAirportByCode(flightR.getArrivalAirport().getCode());

            com.justyna.project.model.graph.Flight flightG = a1.connectsWith(a2);
            flightG.setCode(flightR.getId());

            airportRepositoryG.save(a1);
            airportRepositoryG.save(a2);
        });
    }


    public List<List<List<Flight>>> getFlights(com.justyna.project.model.relational.Airport departAirport,
                                               com.justyna.project.model.relational.Airport arrivalAirport) {
        List<List<List<Flight>>> flights = new ArrayList<>();
        List<List<Flight>> onePath = new ArrayList<>();
        Iterable<Map<String, InternalPath>> results =
                flightGraphRepository.findTheShortest(departAirport.getCode(), arrivalAirport.getCode());

        for (Map<String, InternalPath> row : results) {

            row.get("p").forEach(r -> {
                List<Flight> flight = new ArrayList<>();
                flight.add(flightRelRepository.findById(r.relationship().get("code").asLong()).orElse(new Flight()));
                onePath.add(flight);
            });
            flights.add(onePath);
        }

        return flights;
    }


    public List<List<List<Flight>>> findTheAvailableByTime(com.justyna.project.model.relational.Airport departAirport,
                                                           com.justyna.project.model.relational.Airport arrivalAirport) {

//        List<List<List<Flight>>> candidates = getFlights(departAirport, arrivalAirport);
//        List<List<List<Flight>>> proposition = new ArrayList<>();
//
//        for(List<List<Flight>> onePath : candidates) {
//            List<List<Flight>> paths_accepted = new ArrayList<>();
//            for (List<Flight> flight_legs : onePath) {
//                for (int i = 1; i < flight_legs.size(); i++) {
//                    LocalDateTime departTime = LocalDateTime.parse(flight_legs.get(i).getDepartureTimeUTC());
//                    LocalDateTime arrivalTime = LocalDateTime.parse(flight_legs.get(i - 1).getArrivalTimeUTC());
//                    if (!departTime.isAfter(arrivalTime)) break;
//                    else paths_accepted.add(flight_legs);
//                }
//                proposition.add(paths_accepted);
//            }
//        }


        return new ArrayList<>();
    }
}
