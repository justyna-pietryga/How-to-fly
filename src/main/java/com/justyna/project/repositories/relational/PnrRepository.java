package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.Pnr;
import com.justyna.project.model.relational.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PnrRepository extends CrudRepository<Pnr, Long> {
    @Override
    Optional<Pnr> findById(Long aLong);

    List<Pnr> findByUser(User user);
}
