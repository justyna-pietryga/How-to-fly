package com.justyna.project.model.graph;

import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "FLIGHT_TO")
public class Flight {
    @Id
    @GeneratedValue
    private Long id;

    public double distance;

    @StartNode
    public Airport departureAirport;

    @EndNode
    public Airport arrivalAirport;

    public Flight() {
    }

    public Flight(Airport departureAirport, Airport arrivalAirport, double distance) {
        this.distance = distance;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
    }
}
