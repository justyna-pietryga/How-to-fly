package com.justyna.project.model.relational;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private double latitude;
    private double longitude;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "city", cascade = CascadeType.ALL)
    private Set<Airport> airport;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    public City(String name) {
        this.name = name;
    }

    public City(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public City() {
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                '}';
    }
}
