package com.justyna.project.services;

import com.justyna.project.model.relational.Flight;
import com.justyna.project.model.relational.FlightLeg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(value = AccessLevel.PRIVATE)
public class FlightSearcher {
    private List<Flight> flights;

    public FlightSearcher(List<Flight> flights) {
        this.flights = flights;
    }

    public FlightSearcher sortByTimeOfFlight() {
        List<Flight> allFlights = this.getFlights();
        allFlights.sort(this::compare);

        System.out.println("sortByTimeOfFlight\n " + allFlights);
        val result = new FlightSearcher(allFlights);
        System.out.println("check \n " + result);
        System.out.println("check2 \n " + result.getFlights());
        return result;
    }


    public FlightSearcher findTheAvailableByTime(int minutesToTransfer) {

        List<Flight> candidates = this.getFlights();
        List<Flight> proposition = new ArrayList<>();

        for (Flight onePath : candidates) {
            List<FlightLeg> flightLegs = onePath.getFlightLegs();
            boolean flag = false;
            if (flightLegs.size() == 1) return new FlightSearcher(candidates);
            for (int i = 1; i < flightLegs.size(); i++) {
                ZonedDateTime departTime = ZonedDateTime.parse(flightLegs.get(i).getDepartureTimeUTC());
                ZonedDateTime arrivalTime = ZonedDateTime.parse(flightLegs.get(i - 1).getArrivalTimeUTC());
                if (!departTime.isAfter(arrivalTime.minusMinutes(minutesToTransfer))) {
                    flag = false;
                    break;
                } else flag = true;
            }
            if (flag) proposition.add(onePath);
        }

        System.out.println("findTheAvailableByTime\n " + proposition);

        return new FlightSearcher(proposition);
    }


    public FlightSearcher getWithoutRedundance() {

        List<Flight> candidates = this.getFlights();
        List<Flight> proposition = new ArrayList<>();

        for (Flight flight : candidates) {
            List<FlightLeg> flightLegs = flight.getFlightLegs();
            if (flightLegs.size() == 1) return new FlightSearcher(candidates);
            boolean flag = false;
            for (int i = 0; i < flightLegs.size(); i++) {
                for (int j = 0; j < flightLegs.size(); j++) {
                    if (i != j && (flightLegs.get(i).getDepartureAirport().getCode().equals(flightLegs.get(j).getArrivalAirport().getCode()) && i < j)) {
                        flag = false;
                        break;
                    } else flag = true;
                }
                if (!flag) break;
            }
            if (flag) proposition.add(flight);
        }

        System.out.println("getWithoutRedundance\n " + proposition);

        return new FlightSearcher(proposition);
    }


    private int compare(Flight flight1, Flight flight2) {
        ZonedDateTime flight1start = ZonedDateTime.parse(flight1.getFlightLegs().get(0).getDepartureTimeUTC());
        ZonedDateTime flight1stop = ZonedDateTime.parse(flight1.getFlightLegs().get(flight1.getFlightLegs().size() - 1).getArrivalTimeUTC());
        ZonedDateTime flight2start = ZonedDateTime.parse(flight2.getFlightLegs().get(0).getDepartureTimeUTC());
        ZonedDateTime flight2stop = ZonedDateTime.parse(flight2.getFlightLegs().get(flight2.getFlightLegs().size() - 1).getArrivalTimeUTC());

        return Long.compare(Duration.between(flight1start, flight1stop).toMinutes(), Duration.between(flight2start, flight2stop).toMinutes());
    }
}
