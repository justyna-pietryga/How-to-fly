package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.FlightLeg;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@CrossOrigin
public interface FlightLegRelRepository extends CrudRepository<FlightLeg, Long> {
    @Override
    Optional<FlightLeg> findById(Long aLong);
}
