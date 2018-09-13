package com.justyna.project.model.other;

import com.justyna.project.model.relational.Airport;
import lombok.Data;

import static com.justyna.project.model.other.TimeMode.LOCAL;
import static com.justyna.project.model.other.TimeMode.UTC;

@Data
public class FlightLegDto {
    private Airport departureAirport;
    private Airport arrivalAirport;
    private String departureTime;
    private String arrivalTime;
    private String timeMode;


    public TimeMode getTimeMode() {
        if (timeMode.equals("LOCAL")) return LOCAL;
        else return UTC;
    }
}
