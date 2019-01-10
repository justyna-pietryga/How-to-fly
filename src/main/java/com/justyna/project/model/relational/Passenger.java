package com.justyna.project.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Passenger() {
    }

    private String name;
    private String surname;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "address_id")
//    private Address address;

    private String pesel;
    private String telephone;

    @ManyToOne
    @JoinColumn(name = "pnr_id")
    private Pnr pnr;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reservation> flightLegDetails;

//    @ManyToOne
//    @JoinColumn(name = "place_id")
//    private Place place;
}
