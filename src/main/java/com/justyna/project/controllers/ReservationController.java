package com.justyna.project.controllers;

import com.justyna.project.model.other.reservationHistory.PassengerDetail;
import com.justyna.project.model.other.reservationHistory.PlaceDto;
import com.justyna.project.model.other.reservationHistory.PnrDetailDto;
import com.justyna.project.model.other.reservationHistory.ReservationHistory;
import com.justyna.project.model.relational.*;
import com.justyna.project.repositories.relational.*;
import com.justyna.project.security.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private static Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final PassengerRepository passengerRepository;
    private final FlightLegRelRepository flightLegRelRepository;
    private final PlaceRepository placeRepository;
    private final ReservationRepository reservationRepository;
    private final PnrRepository pnrRepository;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    public ReservationController(PassengerRepository passengerRepository, FlightLegRelRepository flightLegRelRepository,
                                 PlaceRepository placeRepository, ReservationRepository reservationRepository, PnrRepository pnrRepository, TokenProvider tokenProvider, UserRepository userRepository) {
        this.passengerRepository = passengerRepository;
        this.flightLegRelRepository = flightLegRelRepository;
        this.placeRepository = placeRepository;
        this.reservationRepository = reservationRepository;
        this.pnrRepository = pnrRepository;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

//    @CrossOrigin
//    @PreAuthorize("hasRole('USER')")
//    @RequestMapping(value = "/{flightLegId}", method = RequestMethod.POST)
//    public ResponseEntity<Iterable<Reservation>> addReservation(@PathVariable Long flightLegId, @RequestBody List<PassengerDetail> passengerDetails) {
//        List<Reservation> flightLegDetailsList = new ArrayList<>();
//        for (PassengerDetail passengerDetail : passengerDetails) {
//            Passenger passenger = passengerDetail.getPassenger();
//            passengerRepository.save(passenger);
//            flightLegDetailsList.add(new Reservation(passenger, placeRepository.findById(passengerDetail.getPlaceId()).get(),
//                    flightLegRelRepository.findById(flightLegId).get()));
//        }
//
//        return new ResponseEntity<>(reservationRepository.saveAll(flightLegDetailsList), HttpStatus.OK);
//    }

    private void setReservationHistory(Long pnrId, List<ReservationHistory> reservationHistories) {
        Pnr pnr = pnrRepository.findById(pnrId).orElseGet(null);
        logger.info(pnr.toString());
        List<Passenger> passengers = passengerRepository.findByPnr(pnr);
        List<PnrDetailDto> pnrPlaces = new ArrayList<>();

        for (Passenger passenger1 : passengers) {
            List<Reservation> reservations = reservationRepository.findByPassenger(passenger1);
            Set<Passenger> passengersSet = reservations.stream().map(Reservation::getPassenger).collect(Collectors.toSet());
            logger.info("psnSet: " + passengersSet);
            passengersSet.forEach(passenger -> {
                List<Reservation> reservationForOnePsn = reservations.stream()
                        .filter(reservation -> reservation.getPassenger().equals(passenger)).collect(Collectors.toList());
                logger.info("reservationForOnePsn: " + reservationForOnePsn);
                List<PlaceDto> places = new ArrayList<>();
                reservationForOnePsn.forEach(reservation -> {
                    PlaceDto placeDto = new PlaceDto(reservation.getPlace().getId(), reservation.getFlightLeg().getId());
                    logger.info("placeDto: " + placeDto);
                    places.add(placeDto);
                });

                PnrDetailDto pnrDetailDto = new PnrDetailDto(passenger, places);
                logger.info("pnrDetailDto: " + pnrDetailDto);
                pnrPlaces.add(pnrDetailDto);
            });
        }
        reservationHistories.add(new ReservationHistory(pnr, pnrPlaces));
    }

    @CrossOrigin
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(path = "/pnr-reservation", method = RequestMethod.GET)
    public ResponseEntity<List<ReservationHistory>> getReservation() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername);
        List<Pnr> pnrsForUser = pnrRepository.findByUser(currentUser);
        List<ReservationHistory> reservationHistories = new ArrayList<>();

        pnrsForUser.forEach(pnr -> setReservationHistory(pnr.getId(), reservationHistories));

        logger.info("pnrPlaces: " + reservationHistories);

        return new ResponseEntity<>(reservationHistories, HttpStatus.OK);
    }

    @CrossOrigin
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Iterable<Reservation>> addReservation(
            @RequestBody Collection<PassengerDetail> passengerDetails,
            @RequestHeader("Authorization") String token) {
        List<Reservation> reservations = new ArrayList<>();
        Pnr pnr = new Pnr();
        pnr.setCode(new String(new byte[8], Charset.forName("UTF-8")));
        pnrRepository.save(pnr);
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        for (PassengerDetail passengerDetail : passengerDetails) {
//            Passenger passenger = passengerDetail.getPassenger();
            Passenger passenger = passengerRepository.findExistOne(pnr, passengerDetail.getPassenger().getPesel()).orElse(passengerDetail.getPassenger());
            passenger.setPnr(pnr);
            passengerRepository.save(passenger);
            reservations.add(new Reservation(passenger, placeRepository.findById(passengerDetail.getPlaceId()).get(),
                    flightLegRelRepository.findById(passengerDetail.getLegId()).get()));
            pnr.setUser(userRepository.findByUsername(currentUsername));
            pnrRepository.save(pnr);


            logger.info("token " + token);
            logger.info("token " + SecurityContextHolder.getContext().getAuthentication().getName());
//);
//            logger.info("username " + tokenProvider.getUsernameFromToken(token.substring(7, token.length()-1)));
        }

        return new ResponseEntity<>(reservationRepository.saveAll(reservations), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "places/{flightLegId}", method = RequestMethod.GET)
    public ResponseEntity<Iterable<Place>> getPlacesOnSpecificFlightLeg(@PathVariable Long flightLegId) {
        List<Place> places = reservationRepository.findPlacesByFlightLegId(flightLegId);
        return new ResponseEntity<>(places, HttpStatus.OK);
    }
}
