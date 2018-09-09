package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.FlightLeg;
import org.springframework.data.repository.CrudRepository;

public interface FlightRelRepository extends CrudRepository<FlightLeg, Long> {

}
