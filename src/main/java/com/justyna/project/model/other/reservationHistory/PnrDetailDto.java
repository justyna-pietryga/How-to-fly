package com.justyna.project.model.other.reservationHistory;

import com.justyna.project.model.relational.Passenger;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PnrDetailDto {
    private Passenger passenger;
    private List<PlaceDto> placesOnLegs;
}
