package com.justyna.project.model.relational;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Airplane {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Airplane() {
    }

    public Airplane(String code, int capacity) {
        this.code = code;
        this.capacity = capacity;
    }

    private String code;
    private int capacity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "airplane", cascade = CascadeType.ALL)
    List<Place> places = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "airplane", cascade = CascadeType.ALL)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    List<FlightLeg> flightLegs = new ArrayList<>();
}
