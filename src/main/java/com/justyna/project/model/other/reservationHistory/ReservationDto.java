package com.justyna.project.model.other.reservationHistory;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class ReservationDto {
    private PassengerInfo passengerInfo;
    private ArrayList<PassengerDetail> pnrPassengersDetails;

    public ReservationDto(PassengerInfo passengerInfo, ArrayList<PassengerDetail> pnrPassengersDetails) {
        this.passengerInfo = passengerInfo;
        this.pnrPassengersDetails = pnrPassengersDetails;
    }

    public ReservationDto() {
    }
}
