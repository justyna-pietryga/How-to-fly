package com.justyna.project.controllers;

import com.justyna.project.Exceptions.WrongTimeModeException;
import com.justyna.project.model.other.TimeMode;
import com.justyna.project.model.relational.Airport;
import com.justyna.project.model.relational.City;
import com.justyna.project.model.relational.Flight;
import com.justyna.project.model.relational.FlightLeg;
import com.justyna.project.repositories.relational.AirportRelRepository;
import com.justyna.project.repositories.relational.CityRelRepository;
import com.justyna.project.repositories.relational.FlightLegRelRepository;
import com.justyna.project.repositories.relational.FlightRepository;
import com.justyna.project.services.FlightsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightsService flightsService;
    private final FlightRepository flightRepository;
    private final FlightLegRelRepository flightLegRelRepository;
    private final AirportRelRepository airportRelRepository;
    private final CityRelRepository cityRelRepository;

    public FlightController(FlightsService flightsService, FlightRepository flightRepository, FlightLegRelRepository flightLegRelRepository, AirportRelRepository airportRelRepository, CityRelRepository cityRelRepository) {
        this.flightsService = flightsService;
        this.flightRepository = flightRepository;
        this.flightLegRelRepository = flightLegRelRepository;
        this.airportRelRepository = airportRelRepository;
        this.cityRelRepository = cityRelRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @RequestMapping(path = "/search2/departureId={departureAirportId},arrivalId={arrivalAirportId}", method = RequestMethod.GET)
    public ResponseEntity<List<Flight>> search2(@PathVariable Long departureAirportId, @PathVariable Long arrivalAirportId) {
        Optional<Airport> departure = airportRelRepository.findById(departureAirportId);
        Optional<Airport> arrival = airportRelRepository.findById(arrivalAirportId);
        if (departure.isPresent() && arrival.isPresent()) {
            return new ResponseEntity<>(
                    flightsService.getOptimalFlightsByAirports(departure.get(), arrival.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>((List<Flight>) null, HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin
    @RequestMapping(path = "/search/departureId={departureCitytId},arrivalId={arrivalCityId}", method = RequestMethod.GET)
    public ResponseEntity<List<Flight>> search(@PathVariable Long departureCitytId, @PathVariable Long arrivalCityId) {
        Optional<City> departure = cityRelRepository.findById(departureCitytId);
        Optional<City> arrival = cityRelRepository.findById(arrivalCityId);
        if (departure.isPresent() && arrival.isPresent()) {
            List<Flight> flights = flightsService.getOptimalFlightsByCities(departure.get(), arrival.get());
            flightRepository.saveAll(flights);
            Set<FlightLeg> flightLegs = new HashSet<>();

            flights.forEach((flight -> {
                for (FlightLeg flightLeg : flight.getFlightLegs()) {
                    flightLeg.getFlights().add(flight);
                    flightLegs.add(flightLeg);
                }
            }));

            flightLegRelRepository.saveAll(flightLegs);

            return new ResponseEntity<>(flights, HttpStatus.OK);
        }
        return new ResponseEntity<>((List<Flight>) null, HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin
    @RequestMapping(path = "/search3/departureId={departureCitytId},arrivalId={arrivalCityId}", method = RequestMethod.GET)
    public ResponseEntity<List<Flight>> search3(@PathVariable Long departureCitytId, @PathVariable Long arrivalCityId,
                                                @RequestParam String departDate, @RequestParam String arrivalDate, @RequestParam String timeMode,
                                                @RequestParam int amountOfPassengers, @RequestParam int child,
                                                @RequestParam int priceMin, @RequestParam int priceMax)
            throws WrongTimeModeException, IllegalArgumentException {
        Optional<City> departure = cityRelRepository.findById(departureCitytId);
        Optional<City> arrival = cityRelRepository.findById(arrivalCityId);
        if (departure.isPresent() && arrival.isPresent()) {
            try {
                TimeMode timeMode1 = TimeMode.convert(timeMode);

//                List<Flight> flights = flightsService.getOptimalFlightsByCitiesAndDates(departure.get(), arrival.get(), departDate, arrivalDate, timeMode1);
                List<Flight> flights = flightsService.getOptimalFlightsByCriteria(departure.get(), arrival.get(),
                        departDate, arrivalDate, timeMode1, (double) priceMin, (double) priceMax, amountOfPassengers, child);

                System.out.println("ready: " + flights);
                flightRepository.saveAll(flights);
                Set<FlightLeg> flightLegs = new HashSet<>();

                flights.forEach((flight -> {
                    flight.setPrice(flight.getPriceForAllPassengers(amountOfPassengers, child));
                    for (FlightLeg flightLeg : flight.getFlightLegs()) {
                        flightLeg.getFlights().add(flight);
                        flightLegs.add(flightLeg);
                    }
                }));

                flightLegRelRepository.saveAll(flightLegs);

                return new ResponseEntity<>(flights, HttpStatus.OK);
            } catch (WrongTimeModeException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>((List<Flight>) null, HttpStatus.BAD_REQUEST);
    }
}
