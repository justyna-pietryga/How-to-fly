package com.justyna.project.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.justyna.project.model.other.CabinClass;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Place() {
    }

    public Place(String code) {
        this.code = code;
    }

    private String code;

    @Enumerated(EnumType.STRING)
    private CabinClass cabinClass;

    @ManyToOne
    @JoinColumn(name = "airplane_id")
    @JsonIgnore
    private Airplane airplane;

//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//    @JsonIdentityReference(alwaysAsId = true)
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "place", cascade = CascadeType.ALL)
//    private Set<Passenger> passengers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "place", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reservation> flightLegDetails;

}
