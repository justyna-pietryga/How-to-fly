package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.FlightLeg;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
public interface FlightLegRelRepository extends CrudRepository<FlightLeg, Long> {

}
