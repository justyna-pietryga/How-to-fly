package com.justyna.project.model.graph;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.*;

@Getter
@Setter
@RelationshipEntity(type = "FLIGHT_TO")
public class Flight {
    @Id
    @GeneratedValue
    private Long id;

    private long departDate;
    private long arrivalDate;
    private Long code;
    private double distance;

    @StartNode
    private Airport departureAirport;

    @EndNode
    private Airport arrivalAirport;

    public Flight() {
    }

    public Flight(Airport departureAirport, Airport arrivalAirport, double distance) {
        this.distance = distance;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
    }

    public Flight(Airport departureAirport, Airport arrivalAirport, double distance, long departDate) {
        this.distance = distance;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departDate = departDate;
    }
}
