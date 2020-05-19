package com.justyna.project.repositories.relational;

import com.justyna.project.model.relational.Passenger;
import com.justyna.project.model.relational.Place;
import com.justyna.project.model.relational.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin
public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    @Query("select r.place from Reservation r where r.flightLeg.id = ?1")
    List<Place> findPlacesByFlightLegId(Long flightLeg_id);

    List<Reservation> findByPassenger(Passenger passenger);
}
