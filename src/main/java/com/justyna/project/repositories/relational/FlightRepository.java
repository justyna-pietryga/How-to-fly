package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.Flight;
import org.springframework.data.repository.CrudRepository;

public interface FlightRepository extends CrudRepository<Flight, Long> {
}
