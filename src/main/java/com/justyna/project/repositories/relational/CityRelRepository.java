package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.City;
import org.springframework.data.repository.CrudRepository;

public interface CityRelRepository extends CrudRepository<City, Long> {
}
