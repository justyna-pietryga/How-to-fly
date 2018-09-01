package com.justyna.project;

import com.justyna.project.model.graph.Airport;
import com.justyna.project.model.relational.Shit;
import com.justyna.project.repositories.graph.AirportRepository;
import com.justyna.project.repositories.graph.FlightRepository;
import com.justyna.project.repositories.relational.ShitRepository;
import org.neo4j.driver.internal.InternalPath;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(ShitRepository shitRepository, AirportRepository airportRepository, FlightRepository flightRepository) {
        return args -> {

            shitRepository.deleteAll();
            shitRepository.save(new Shit("On the top of the world"));
            airportRepository.deleteAll();

            Airport londonCanada = new Airport("London Airport", "YXU", 43.035599, -81.1539);
            Airport london = new Airport("London Luton Airport", "LTN", 51.874698638916016, -0.36833301186561584);
            Airport cracow = new Airport("Balice", "KRK", 50.077701568603516, 19.784799575805664);
            Airport barcelona = new Airport("Barcelona International Airport", "BCN", 41.297100067139, 2.0784599781036);
            Airport moscow = new Airport("Vnukovo International Airport", "VKO", 55.5914993286, 37.2615013123);
            Airport chicago = new Airport("Chicago Midway International Airport", "MDW", 41.7859992980957, -87.75240325927734);

            List<Airport> airports = Arrays.asList(london, londonCanada, cracow, barcelona, moscow, chicago);

            cracow.connectsWith(chicago);
            barcelona.connectsWith(london);
            londonCanada.connectsWith(chicago);
            london.connectsWith(moscow);
            barcelona.connectsWith(chicago);
            london.connectsWith(barcelona);
            cracow.connectsWith(london);
            moscow.connectsWith(chicago);
            chicago.connectsWith(londonCanada);

            airportRepository.saveAll(airports);

            Iterable<Map<String, InternalPath>> results =
                    flightRepository.findTheShortest(london.getCode(), londonCanada.getCode());

            for (Map<String, InternalPath> row : results) {
                    row.get("p").forEach(r ->
                        System.out.println(r.start().get("code") + "->" + r.end().get("code") + r.relationship().id()));
                    System.out.println("_________________");

            }

        };
    }
}
