package com.justyna.project.repositories.graph;

import com.justyna.project.model.graph.Flight;
import org.neo4j.driver.internal.InternalPath;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Map;

public interface FlightGraphRepository extends Neo4jRepository<Flight, Long> {
    @Query("MATCH p=(departure:Airport {code: {airportDepartureCode}})-[flights:FLIGHT_TO*]->" +
            "(arrival:Airport {code: {airportArrivalCode}}) WITH p, " +
            "reduce(dist=0, flight IN flights | dist+flight.distance) AS how_long RETURN p, how_long ORDER BY how_long ASC")
    Iterable<Map<String, InternalPath>> findTheShortest(@Param("airportDepartureCode") String airportDepartureCode,
                                                        @Param("airportArrivalCode") String airportArrivalCode);
}
