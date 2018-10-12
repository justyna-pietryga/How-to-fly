package com.justyna.project.model.relational;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class Pnr {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Pnr() {
    }

    public Pnr(Long id) {
        this.id = id;
    }

    private String code;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id", cascade = CascadeType.ALL)
    private List<Passenger> passengers;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    //ToDo pnr i pasenger code, generated chars
}
