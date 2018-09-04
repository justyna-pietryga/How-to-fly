package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.Flight;
import org.springframework.data.repository.CrudRepository;

public interface FlightRelRepository extends CrudRepository<Flight, Long> {

}
