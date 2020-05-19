package com.justyna.project.model.other.reservationHistory;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceDto {
    private Long placeId;
    private Long flightLegId;
}
