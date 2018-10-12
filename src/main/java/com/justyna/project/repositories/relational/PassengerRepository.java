package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.Passenger;
import org.springframework.data.repository.CrudRepository;

public interface PassengerRepository extends CrudRepository<Passenger, Long> {
}
