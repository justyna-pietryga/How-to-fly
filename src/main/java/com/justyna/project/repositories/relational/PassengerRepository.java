package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.Passenger;
import com.justyna.project.model.relational.Pnr;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PassengerRepository extends CrudRepository<Passenger, Long> {
    List<Passenger> findByPnr(Pnr pnr);

    @Query("select p from Passenger p where p.pnr = ?1 and p.pesel like ?2")
    Optional<Passenger> findExistOne(Pnr pnr, String pesel);
}
