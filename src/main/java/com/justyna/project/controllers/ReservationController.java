package com.justyna.project.controllers;

import com.justyna.project.model.other.reservationHistory.*;
import com.justyna.project.model.relational.*;
import com.justyna.project.repositories.relational.*;
import com.justyna.project.security.TokenProvider;
import com.justyna.project.services.ReservationService;
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
    private final ReservationService reservationService;
    private final PassengerRepository passengerRepository;
    private final FlightLegRelRepository flightLegRelRepository;
    private final PlaceRepository placeRepository;
    private final ReservationRepository reservationRepository;
    private final PnrRepository pnrRepository;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    public ReservationController(PassengerRepository passengerRepository, FlightLegRelRepository flightLegRelRepository,
                                 PlaceRepository placeRepository, ReservationService reservationService, ReservationRepository reservationRepository, PnrRepository pnrRepository, TokenProvider tokenProvider, UserRepository userRepository) {
        this.passengerRepository = passengerRepository;
        this.flightLegRelRepository = flightLegRelRepository;
        this.placeRepository = placeRepository;
        this.reservationRepository = reservationRepository;
        this.pnrRepository = pnrRepository;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.reservationService = reservationService;
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Iterable<Reservation>> addReservation(
            @RequestBody ReservationDto reservationDto) {

        double price = calculatePrice(reservationDto);
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Reservation> reservations =
                reservationService.makeReservation(reservationDto.getPnrPassengersDetails(), currentUsername, price);

        return new ResponseEntity<>(reservationRepository.saveAll(reservations), HttpStatus.OK);
    }

    private double calculatePrice(ReservationDto reservationDto) {
        Collection<PassengerDetail> pnrDetailDtos = reservationDto.getPnrPassengersDetails();
        double price = 0;
        PassengerInfo passengerInfo = reservationDto.getPassengerInfo();
        int passengerCount = passengerInfo.getPassengerCount();
        int child = passengerInfo.getChild();
        for (PassengerDetail pnrDetail : pnrDetailDtos) {
            FlightLeg leg = flightLegRelRepository.findById(pnrDetail.getLegId()).get();
            price += leg.getPriceForAllPassengers(passengerCount, child);
        }

        return price;
    }

    @CrossOrigin
    @RequestMapping(value = "places/{flightLegId}", method = RequestMethod.GET)
    public ResponseEntity<Iterable<Place>> getPlacesOnSpecificFlightLeg(@PathVariable Long flightLegId) {
        List<Place> places = reservationRepository.findPlacesByFlightLegId(flightLegId);
        return new ResponseEntity<>(places, HttpStatus.OK);
    }
}
