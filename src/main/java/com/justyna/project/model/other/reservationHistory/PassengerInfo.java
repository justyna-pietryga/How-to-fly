package com.justyna.project.model.other.reservationHistory;

import lombok.Data;

@Data
public class PassengerInfo {
    int passengerCount;
    int child;

    public PassengerInfo(int passengerCount, int child) {
        this.passengerCount = passengerCount;
        this.child = child;
    }

    public PassengerInfo() {}
}
