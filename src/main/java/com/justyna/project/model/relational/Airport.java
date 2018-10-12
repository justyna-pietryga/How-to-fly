package com.justyna.project.model.relational;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "airport")
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;
    private String name;
    private double latitude;
    private double longitude;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    private String timeZone;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "id", cascade = CascadeType.ALL)
    private List<FlightLeg> flightLeg;

    public Airport(String code) {
        this.code = code;
    }

    public Airport(String code, String name, double latitude, double longitude, City city, String timeZone) {
        this.code = code;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.timeZone = timeZone;
    }

    public Airport() {
    }

    @Override
    public String toString() {
        return "Airport{" +
                ", name='" + code + '\'' +
                '}';
    }
}
