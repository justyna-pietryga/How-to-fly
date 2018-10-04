package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.Airport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
public interface AirportRelRepository extends CrudRepository<Airport, Long> {
}
