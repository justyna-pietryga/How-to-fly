package com.justyna.project.model.relational;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String name;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "country", cascade = CascadeType.ALL)
    private Set<City> cities;

    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }
}
