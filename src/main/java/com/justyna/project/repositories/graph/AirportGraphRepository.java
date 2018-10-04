package com.justyna.project.repositories.graph;

import com.justyna.project.model.graph.Airport;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface AirportGraphRepository extends Neo4jRepository<Airport, Long> {
    Airport findAirportByCode(String code);
}
