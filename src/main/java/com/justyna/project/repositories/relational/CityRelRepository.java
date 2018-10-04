package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@CrossOrigin
public interface CityRelRepository extends CrudRepository<City, Long> {
    Optional<City> findByName(String name);
}
