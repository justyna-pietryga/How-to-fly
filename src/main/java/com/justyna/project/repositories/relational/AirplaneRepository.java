package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.Airplane;
import org.springframework.data.repository.CrudRepository;

public interface AirplaneRepository extends CrudRepository<Airplane, Long> {
}
