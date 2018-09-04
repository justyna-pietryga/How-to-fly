package com.justyna.project;

import com.justyna.project.model.relational.Airport;
import com.justyna.project.model.relational.City;
import com.justyna.project.model.relational.Flight;
import com.justyna.project.repositories.graph.AirportGraphRepository;
import com.justyna.project.repositories.graph.FlightGraphRepository;
import com.justyna.project.repositories.relational.AirportRelRepository;
import com.justyna.project.repositories.relational.CityRelRepository;
import com.justyna.project.repositories.relational.FlightRelRepository;
import com.justyna.project.services.general.FlightsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(AirportGraphRepository airportRepository,
                           FlightGraphRepository flightGraphRepository, FlightRelRepository flightRelRepository,
                           AirportRelRepository airportRelRepository,
                           CityRelRepository cityRepository, FlightsService flightsServices
    ) {
        return args -> {
            airportRepository.deleteAll();
            flightGraphRepository.deleteAll();
            Map<String, City> cityMap = new HashMap<>();
            cityMap.put("Cracow", new City("Cracow"));
            cityMap.put("Chicago", new City("Chicago"));
            cityMap.put("LondonCanada", new City("LondonCanada"));
            cityMap.put("Barcelona", new City("Barcelona"));
            cityMap.put("Moscow", new City("Moscow"));
            cityMap.put("London", new City("London"));

            cityMap.forEach((s, city) -> cityRepository.save(city));

            com.justyna.project.model.relational.Airport cracowAirport =
                    new com.justyna.project.model.relational.Airport("KRK", "Balice",
                            50.077701568603516, 19.784799575805664, cityMap.get("Cracow"), "UTC+2");


            com.justyna.project.model.relational.Airport chicagoAirport =
                    new com.justyna.project.model.relational.Airport("MDW", "Chicago Midway International Airport",
                            41.7859992980957, -87.75240325927734, cityMap.get("Chicago"), "UTC-5");

            com.justyna.project.model.relational.Airport londonAirport =
                    new com.justyna.project.model.relational.Airport("LTN", "London Luton Airport",
                            51.874698638916016, -0.36833301186561584, cityMap.get("London"), "UTC");

            com.justyna.project.model.relational.Airport barcelonaAirport =
                    new com.justyna.project.model.relational.Airport("BCN", "Barcelona International Airport",
                            41.297100067139, 2.0784599781036, cityMap.get("Barcelona"), "UTC+1");

            com.justyna.project.model.relational.Airport moscowAirport =
                    new com.justyna.project.model.relational.Airport("VKO", "Vnukovo International Airport",
                            55.5914993286, 37.2615013123, cityMap.get("Moscow"), "UTC+3");

            com.justyna.project.model.relational.Airport londonCanadaAirport =
                    new com.justyna.project.model.relational.Airport("YXU", "London Airport",
                            43.035599, -81.1539, cityMap.get("London"), "UTC-4");

            Set<Flight> flights = new HashSet<>();
            flights.add(new Flight(cracowAirport, chicagoAirport, "2018-09-02T22:00", "2018-09-02T22:00"));
            flights.add(new Flight(barcelonaAirport, londonAirport, "2018-09-02T22:00", "2018-09-02T22:00"));
//            flights.add(new Flight(londonCanadaAirport, chicagoAirport, "2018-09-02T22:00", "2018-09-02T22:00"));
            flights.add(new Flight(londonAirport, moscowAirport, "2018-09-02T09:00", "2018-09-02T16:40"));
            flights.add(new Flight(barcelonaAirport, chicagoAirport, "2018-09-02T22:00", "2018-09-02T22:00"));
            flights.add(new Flight(londonAirport, barcelonaAirport, "2018-09-02T22:00", "2018-09-02T22:00"));
            flights.add(new Flight(cracowAirport, londonAirport, "2018-09-02T22:00", "2018-09-02T22:00"));
            flights.add(new Flight(moscowAirport, chicagoAirport, "2018-09-02T17:10", "2018-09-02T18:10"));
            flights.add(new Flight(chicagoAirport, londonCanadaAirport, "2018-09-02T18:40", "2018-09-02T20:40"));

            Set<Airport> airports = new HashSet<>();
            airports.add(cracowAirport);
            airports.add(chicagoAirport);
            airports.add(moscowAirport);
            airports.add(londonAirport);
            airports.add(londonCanadaAirport);
            airports.add(barcelonaAirport);

            flightsServices.translateAirports(airports, flights);

        };
    }
}
