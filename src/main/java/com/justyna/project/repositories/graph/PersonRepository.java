package com.justyna.project.repositories.graph;

import com.justyna.project.model.graph.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;

//@ComponentScan(value = {"com.example.demo.model.graph"})
public interface PersonRepository extends Neo4jRepository<Person, Long> {

    Person findByName(String name);
}