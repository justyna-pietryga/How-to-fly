package com.justyna.project.model.other;

import com.justyna.project.model.relational.Passenger;
import lombok.Getter;

@Getter
public class PassengerDetail {
    private Passenger passenger;
    private long placeId;
}
