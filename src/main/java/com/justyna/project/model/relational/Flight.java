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

    public Flight() {
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "id", cascade = CascadeType.ALL)
    private List<FlightLeg> flightLegs = new ArrayList<>();

    @Override
    public String toString() {
        return "Flight{" +
                "flightLegs=" + flightLegs +
                "}\n\n";
    }
}
