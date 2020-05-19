package com.justyna.project.model.relational;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double price;

    public Flight() {
    }

    //    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flight", cascade = CascadeType.ALL)
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "flights")
    private List<FlightLeg> flightLegs = new ArrayList<>();

    public double getPriceForAllPassengers(int passengersCount, int children) {
        return this.flightLegs.stream().mapToDouble(flightLeg -> flightLeg.getPriceForAllPassengers(passengersCount, children)).sum();
    }

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flight", cascade = CascadeType.ALL)
//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//    @JsonIdentityReference(alwaysAsId = true)
//    private List<Pnr> pnrs = new ArrayList<>();

    @Override
    public String toString() {
        return "Flight{" +
                "flightLegs=" + flightLegs +
                "}\n\n";
    }
}
