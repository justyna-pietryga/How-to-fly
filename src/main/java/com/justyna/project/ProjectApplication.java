package com.justyna.project;

import com.justyna.project.model.other.CabinClass;
import com.justyna.project.model.relational.*;
import com.justyna.project.repositories.graph.AirportGraphRepository;
import com.justyna.project.repositories.graph.FlightGraphRepository;
import com.justyna.project.repositories.relational.AirplaneRepository;
import com.justyna.project.repositories.relational.CityRelRepository;
import com.justyna.project.repositories.relational.CountryRelRepository;
import com.justyna.project.repositories.relational.PlaceRepository;
import com.justyna.project.services.FlightsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;

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
                           PlaceRepository placeRepository,
                           AirplaneRepository airplaneRepository,
                           CityRelRepository cityRepository, FlightsService flightsServices, CountryRelRepository countryRelRepository
    ) {
        return args -> {
            airportRepository.deleteAll();
            flightGraphRepository.deleteAll();

            List<Airplane> airplanes = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                airplanes.add(new Airplane("GA", 100));
            }
            airplaneRepository.saveAll(airplanes);

            for (Airplane airplane : airplanes) {
                List<Place> placeList = new ArrayList<>();
                for (int i = 0; i < airplane.getCapacity(); i++) {
                    Place place = new Place(airplane.getCode() + i);
                    if (i < airplane.getCapacity() * 0.20) place.setCabinClass(CabinClass.A);
                    else place.setCabinClass(CabinClass.B);
                    place.setAirplane(airplane);
                    placeList.add(place);
                }
                placeRepository.saveAll(placeList);
            }


            Map<String, City> cityMap = new HashMap<>();
            Map<String, Country> countryMap = new HashMap<>();
            countryMap.put("Poland", new Country("Poland"));
            countryMap.put("USA", new Country("USA"));
            countryMap.put("Canada", new Country("Canada"));
            countryMap.put("Spain", new Country("Spain"));
            countryMap.put("Russia", new Country("Russia"));
            countryMap.put("UK", new Country("UK"));
            countryMap.forEach((s, country) -> countryRelRepository.save(country));

            cityMap.put("Cracow", new City("Cracow", countryMap.get("Poland")));
            cityMap.put("Chicago", new City("Chicago", countryMap.get("USA")));
            cityMap.put("LondonCanada", new City("LondonCanada", countryMap.get("Canada")));
            cityMap.put("Barcelona", new City("Barcelona", countryMap.get("Spain")));
            cityMap.put("Moscow", new City("Moscow", countryMap.get("Russia")));
            cityMap.put("London", new City("London", countryMap.get("UK")));

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
                            43.035599, -81.1539, cityMap.get("LondonCanada"), "UTC-4");

            Set<FlightLeg> flights = new HashSet<>();

            flights.add(new FlightLeg(cracowAirport, chicagoAirport, "2018-09-02T22:00", "2018-09-02T22:00", LOCAL, airplanes.get(0)));
            flights.add(new FlightLeg(barcelonaAirport, londonAirport, "2018-09-02T06:10", "2018-09-02T07:30", UTC, airplanes.get(1)));
//            flights.add(new Flight(londonCanadaAirport, chicagoAirport, "2018-09-02T22:00", "2018-09-02T22:00"));
            flights.add(new FlightLeg(londonAirport, moscowAirport, "2018-09-02T08:00", "2018-09-02T12:40", UTC, airplanes.get(2)));
            flights.add(new FlightLeg(barcelonaAirport, chicagoAirport, "2018-09-02T09:00", "2018-09-02T17:20", UTC, airplanes.get(3)));
            flights.add(new FlightLeg(londonAirport, barcelonaAirport, "2018-09-02T04:30", "2018-09-02T05:50", UTC, airplanes.get(4)));
            flights.add(new FlightLeg(cracowAirport, londonAirport, "2018-09-02T22:00", "2018-09-02T22:00", LOCAL, airplanes.get(5)));
            flights.add(new FlightLeg(moscowAirport, chicagoAirport, "2018-09-02T13:00", "2018-09-02T22:00", UTC, airplanes.get(6)));
            flights.add(new FlightLeg(chicagoAirport, londonCanadaAirport, "2018-09-02T23:00", "2018-09-03T00:00", UTC, airplanes.get(7)));
            flights.add(new FlightLeg(chicagoAirport, londonCanadaAirport, "2018-09-02T22:30", "2018-09-02T23:30", UTC, airplanes.get(8)));

            Set<Airport> airports = new HashSet<>();
            airports.add(cracowAirport);
            airports.add(chicagoAirport);
            airports.add(moscowAirport);
            airports.add(londonAirport);
            airports.add(londonCanadaAirport);
            airports.add(barcelonaAirport);


            flightsServices.translateAirports(airports, flights);
            System.out.println("Wait for it...");
            System.out.println("Compilation is done");
//            flightsServices.getOptimalFlightsByCitiesAndDates(londonAirport, londonCanadaAirport, "2018-09-02T08:00Z", "2018-09-02T23:30Z", UTC);
//            flightsServices.getOptimalFlightsByCitiesAndDates(cracowAirport, chicagoAirport, "2018-09-02T22:00Z", "2018-09-02T22:00Z", LOCAL);
//            System.out.println("result " + flightsServices.getOptimalFlightsByAirports(londonAirport, londonCanadaAirport));
            // System.out.println(flightsServices.getOptimalFlightsByAirports(londonAirport, londonCanadaAirport));

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