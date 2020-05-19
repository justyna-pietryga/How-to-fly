package com.justyna.project.services;

import com.justyna.project.model.other.reservationHistory.PassengerDetail;
import com.justyna.project.model.relational.Passenger;
import com.justyna.project.model.relational.Pnr;
import com.justyna.project.model.relational.Reservation;
import com.justyna.project.repositories.relational.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class ReservationService {
    private final PassengerRepository passengerRepository;
    private final FlightLegRelRepository flightLegRelRepository;
    private final PlaceRepository placeRepository;
    private final ReservationRepository reservationRepository;
    private final PnrRepository pnrRepository;
    private final UserRepository userRepository;

    public ReservationService(PassengerRepository passengerRepository, FlightLegRelRepository flightLegRelRepository,
                              PlaceRepository placeRepository, ReservationRepository reservationRepository,
                              PnrRepository pnrRepository, UserRepository userRepository) {
        this.passengerRepository = passengerRepository;
        this.flightLegRelRepository = flightLegRelRepository;
        this.placeRepository = placeRepository;
        this.reservationRepository = reservationRepository;
        this.pnrRepository = pnrRepository;
        this.userRepository = userRepository;
    }

    public List<Reservation> makeReservation(Collection<PassengerDetail> passengerDetails, String currentUsername, double price) {
        List<Reservation> reservations = new ArrayList<>();
        Pnr pnr = new Pnr();
        pnr.setCode(new String(new byte[8], Charset.forName("UTF-8")));
        pnrRepository.save(pnr);
        for (PassengerDetail passengerDetail : passengerDetails) {
            Passenger passenger = passengerRepository.findExistOne(pnr, passengerDetail.getPassenger().getPesel()).orElse(passengerDetail.getPassenger());
            passenger.setPnr(pnr);
            passengerRepository.save(passenger);
            reservations.add(new Reservation(passenger, placeRepository.findById(passengerDetail.getPlaceId()).get(),
                    flightLegRelRepository.findById(passengerDetail.getLegId()).get()));
            pnr.setUser(userRepository.findByUsername(currentUsername));
            pnr.setPrice(price);
            pnrRepository.save(pnr);
        }

        return reservations;
    }
}
