package com.justyna.project.model.relational;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Reservation() {
    }

    public Reservation(Passenger passenger, Place place, FlightLeg flightLeg) {
        this.passenger = passenger;
        this.place = place;
        this.flightLeg = flightLeg;
    }

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne
    @JoinColumn(name = "flightLeg_id")
    private FlightLeg flightLeg;
}
