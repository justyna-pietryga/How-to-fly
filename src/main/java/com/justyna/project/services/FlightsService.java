package com.justyna.project.services;

import com.justyna.project.model.other.TimeMode;
import com.justyna.project.model.relational.Airport;
import com.justyna.project.model.relational.City;
import com.justyna.project.model.relational.Flight;
import com.justyna.project.model.relational.FlightLeg;
import com.justyna.project.repositories.graph.AirportGraphRepository;
import com.justyna.project.repositories.graph.FlightGraphRepository;
import com.justyna.project.repositories.relational.AirportRelRepository;
import com.justyna.project.repositories.relational.FlightLegRelRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.neo4j.driver.internal.InternalPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.justyna.project.model.other.TimeMode.UTC;

@Service
@Slf4j
public class FlightsService {

    private static Logger logger = LoggerFactory.getLogger(FlightsService.class);

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
            flightG.setDepartDate(new DateTime(flightR.getDeptTimeDate()).getMillis());
            flightG.setArrivalDate(new DateTime(flightR.getArrivalTimeDate()).getMillis());

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

    public List<Flight> getOptimalFlightsByCitiesAndDates(City departCity, City arrivalCity,
                                                          String departDate, String arrivalDate, TimeMode timeMode) {
        List<Flight> flights = new ArrayList<>();
        departCity.getAirport().forEach(airportD ->
                arrivalCity.getAirport().forEach(airportA ->
                        flights.addAll(getOptimalFlightsByAirportsAndDates(airportD, airportA, departDate, arrivalDate, timeMode))));
        return flights;
    }

    public List<Flight> getOptimalFlightsByAirportsAndDates(Airport departAirport, Airport arrivalAirport,
                                                            String departDate, String arrivalDate, TimeMode timeMode) {
        return algorithmSearcherService.getFlights(
                getFlightsWithDatesFromDB(departAirport, arrivalAirport, departDate, arrivalDate, timeMode));
    }

    private List<Flight> getFlightsFromDB(Airport departAirport, Airport arrivalAirport) {
        List<Flight> flights = new ArrayList<>();

        Iterable<Map<String, InternalPath>> results =
                flightGraphRepository.findTheShortest(departAirport.getCode(), arrivalAirport.getCode());

        System.out.println(results);
        for (Map<String, InternalPath> row : results) {
            Flight onePath = new Flight();
            row.get("p").forEach(r ->
                    onePath.getFlightLegs().add(flightRelRepository.findById(r.relationship().get("code").asLong()).orElse(new FlightLeg())));
            flights.add(onePath);
        }

        return flights;
    }

    private List<Flight> getFlightsWithDatesFromDB(Airport departAirport, Airport arrivalAirport,
                                                   String departDate, String arrivalDate, TimeMode timeMode) {
        List<Flight> flights = new ArrayList<>();

        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm'Z'");
        DateTime depart, arrival;
        if (timeMode.equals(UTC)) {
            depart = new DateTime(new Timestamp(format.parseDateTime(departDate).getMillis()));
            arrival = new DateTime(new Timestamp(format.parseDateTime(arrivalDate).getMillis()));
        } else {
            ZonedDateTime departZonedDateTime, arrivalZonedDateTime;
            String departTimeUTC, arrivalTimeUTC;
            departZonedDateTime = LocalDateTime.parse(departDate.substring(0, departDate.length() - 1)).atZone(ZoneId.of(departAirport.getTimeZone()));
            departTimeUTC = departZonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toString();
            arrivalZonedDateTime = LocalDateTime.parse(arrivalDate.substring(0, arrivalDate.length() - 1)).atZone(ZoneId.of(arrivalAirport.getTimeZone()));
            arrivalTimeUTC = arrivalZonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toString();

            depart = new DateTime(new Timestamp(format.parseDateTime(departTimeUTC).getMillis()));
            arrival = new DateTime(new Timestamp(format.parseDateTime(arrivalTimeUTC).getMillis()));
        }

        logger.info("depart " + depart);
        logger.info("arrival " + arrival);

        Iterable<Map<String, InternalPath>> results =
                flightGraphRepository.findTheShortestWithDatesRange(departAirport.getCode(), arrivalAirport.getCode(), depart.getMillis(),
                        arrival.getMillis());

        System.out.println(results);
        for (Map<String, InternalPath> row : results) {
            Flight onePath = new Flight();
            row.get("p").forEach(r ->
                    onePath.getFlightLegs().add(flightRelRepository.findById(r.relationship().get("code").asLong()).orElse(new FlightLeg())));
            flights.add(onePath);
        }

        return flights;
    }
}
