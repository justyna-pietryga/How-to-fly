package com.justyna.project;

import com.justyna.project.model.graph.Person;
import com.justyna.project.model.relational.Shit;
import com.justyna.project.repositories.graph.PersonRepository;
import com.justyna.project.repositories.relational.ShitRepository;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class ProjectApplication {

	private final static org.slf4j.Logger log = LoggerFactory.getLogger(ProjectApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	@Bean
	CommandLineRunner demo(PersonRepository personRepository, ShitRepository shitRepository) {
		return args -> {

			shitRepository.save(new Shit("On the top of the world"));
			personRepository.deleteAll();

			Person greg = new Person("Justyna");
			Person roy = new Person("Mary");
			Person craig = new Person("Craig");

			List<Person> team = Arrays.asList(greg, roy, craig);

			log.info("Before linking up with Neo4j...");

			team.stream().forEach(person -> log.info("\t" + person.toString()));

			personRepository.save(greg);
			personRepository.save(roy);
			personRepository.save(craig);

			greg = personRepository.findByName(greg.getName());
			greg.worksWith(roy);
			greg.worksWith(craig);
			personRepository.save(greg);

			roy = personRepository.findByName(roy.getName());
			roy.worksWith(craig);
			// We already know that roy works with greg
			personRepository.save(roy);

			// We already know craig works with roy and greg

			log.info("Lookup each person by name...");
			team.stream().forEach(person -> log.info(
					"\t" + personRepository.findByName(person.getName()).toString()));
		};
	}
}
