package com.justyna.project.model.relational;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Address() {
    }

    private String street;
    private String postalCode;
    private String country;

//    @OneToOne(fetch = FetchType.LAZY, cascade =  CascadeType.ALL, mappedBy = "address")
//    private Passenger passenger;
}
