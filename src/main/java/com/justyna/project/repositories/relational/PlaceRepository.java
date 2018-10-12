package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.Place;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlaceRepository extends CrudRepository<Place, Long> {
    @Override
    Optional<Place> findById(Long aLong);
}
