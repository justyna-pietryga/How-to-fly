package com.justyna.project;

import com.justyna.project.model.relational.Airport;
import com.justyna.project.model.relational.City;
import com.justyna.project.model.relational.FlightLeg;
import com.justyna.project.repositories.graph.AirportGraphRepository;
import com.justyna.project.repositories.graph.FlightGraphRepository;
import com.justyna.project.repositories.relational.CityRelRepository;
import com.justyna.project.services.general.FlightsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.justyna.project.model.other.TimeMode.LOCAL;
import static com.justyna.project.model.other.TimeMode.UTC;

@SpringBootApplication
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(AirportGraphRepository airportRepository,
                           FlightGraphRepository flightGraphRepository,
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

            Set<FlightLeg> flights = new HashSet<>();

            flights.add(new FlightLeg(cracowAirport, chicagoAirport, "2018-09-02T22:00", "2018-09-02T22:00", LOCAL));
            flights.add(new FlightLeg(barcelonaAirport, londonAirport, "2018-09-02T06:10", "2018-09-02T07:30", UTC));
//            flights.add(new Flight(londonCanadaAirport, chicagoAirport, "2018-09-02T22:00", "2018-09-02T22:00"));
            flights.add(new FlightLeg(londonAirport, moscowAirport, "2018-09-02T08:00", "2018-09-02T12:40", UTC));
            flights.add(new FlightLeg(barcelonaAirport, chicagoAirport, "2018-09-02T09:00", "2018-09-02T17:20", UTC));
            flights.add(new FlightLeg(londonAirport, barcelonaAirport, "2018-09-02T04:30", "2018-09-02T05:50", UTC));
            flights.add(new FlightLeg(cracowAirport, londonAirport, "2018-09-02T22:00", "2018-09-02T22:00", LOCAL));
            flights.add(new FlightLeg(moscowAirport, chicagoAirport, "2018-09-02T13:00", "2018-09-02T22:00", UTC));
            flights.add(new FlightLeg(chicagoAirport, londonCanadaAirport, "2018-09-02T23:00", "2018-09-03T00:00", UTC));
            flights.add(new FlightLeg(chicagoAirport, londonCanadaAirport, "2018-09-02T22:30", "2018-09-02T23:30", UTC));

            Set<Airport> airports = new HashSet<>();
            airports.add(cracowAirport);
            airports.add(chicagoAirport);
            airports.add(moscowAirport);
            airports.add(londonAirport);
            airports.add(londonCanadaAirport);
            airports.add(barcelonaAirport);

            flightsServices.translateAirports(airports, flights);
            System.out.println(flightsServices.sortByTimeOfFlight(londonAirport, londonCanadaAirport));

        };
    }
}


//flights.add(new FlightLeg(cracowAirport, chicagoAirport, "2018-09-02T22:00", "2018-09-02T22:00", LOCAL));
//        flights.add(new FlightLeg(barcelonaAirport, londonAirport, "2018-09-02T07:10", "2018-09-02T08:30", UTC));
////            flights.add(new Flight(londonCanadaAirport, chicagoAirport, "2018-09-02T22:00", "2018-09-02T22:00"));
//        flights.add(new FlightLeg(londonAirport, moscowAirport, "2018-09-02T09:00", "2018-09-02T16:40", LOCAL));
//        flights.add(new FlightLeg(barcelonaAirport, chicagoAirport, "2018-09-02T13:00", "2018-09-02T23:30", UTC));
//        flights.add(new FlightLeg(londonAirport, barcelonaAirport, "2018-09-02T05:20", "2018-09-02T06:30", UTC));
//        flights.add(new FlightLeg(cracowAirport, londonAirport, "2018-09-02T22:00", "2018-09-02T22:00", LOCAL));
//        flights.add(new FlightLeg(moscowAirport, chicagoAirport, "2018-09-02T14:10", "2018-09-02T23:10", UTC));
//        flights.add(new FlightLeg(chicagoAirport, londonCanadaAirport, "2018-09-02T23:40", "2018-09-02T00:40", UTC));