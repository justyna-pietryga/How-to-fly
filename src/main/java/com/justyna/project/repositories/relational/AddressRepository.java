package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}
