package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.Country;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
public interface CountryRelRepository extends CrudRepository<Country, Long> {
}
