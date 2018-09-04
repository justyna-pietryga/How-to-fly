package com.justyna.project.model.graph;

import lombok.Getter;
import lombok.Setter;
import org.geotools.referencing.GeodeticCalculator;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NodeEntity
public class Airport {

    public Airport() {
    }

    public Airport(String name, String code, double latitude, double longitude) {
        this.name = name;
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String code;

    private double latitude;
    private double longitude;


    @Relationship(type = "FLIGHT_TO")
    private List<Flight> connections;

    public Flight connectsWith(Airport airport) {
        if (connections == null) {
            connections = new ArrayList<>();
        }
        Flight flight = new Flight(this, airport, this.getDistanceInKilometers(airport.getPosition()));
        connections.add(flight);

        return flight;
    }

    private Point2D getPosition() {
        return new Point2D.Double(latitude, longitude);
    }

    private double getDistanceInKilometers(Point2D destPoint) {
        GeodeticCalculator calculator = new GeodeticCalculator();
        calculator.setStartingGeographicPoint(this.getPosition());
        calculator.setDestinationGeographicPoint(destPoint);
        return calculator.getOrthodromicDistance() / 1000;
    }

//    @Override
//    public String toString() {
//        return this.code + "'s connectins => "
//                + Optional.ofNullable(this.connections).orElse(
//                Collections.emptySet()).stream()
//                .map(Airport::getCode)
//                .collect(Collectors.toList());
//    }
}
