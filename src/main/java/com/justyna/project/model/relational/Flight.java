package com.justyna.project.model.relational;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "flight")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Flight() {
    }

    @ManyToOne
    @JoinColumn(name = "departure_airport_id")
    private Airport departureAirport;
    @ManyToOne
    @JoinColumn(name = "arrival_airport_id")
    private Airport arrivalAirport;

    @Setter(AccessLevel.PRIVATE)
    private String departureTimeUTC;
    @Setter(AccessLevel.PRIVATE)
    private String arrivalTimeUTC;
    @Setter(AccessLevel.PRIVATE)
    private String departureTimeLocale;
    @Setter(AccessLevel.PRIVATE)
    private String arrivalTimeLocale;


    public Flight(Airport departureAirport, Airport arrivalAirport, String departureTime, String arrivalTime) {
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        setDepartureTime(departureTime);
        setArrivalTime(arrivalTime);
    }

    public void setDepartureTime(String localeTime) {
        ZonedDateTime departureTime = LocalDateTime.parse(localeTime).atZone(ZoneId.of(departureAirport.getTimeZone()));
        departureTimeLocale = String.valueOf(departureTime);
        departureTimeUTC = departureTime.withZoneSameInstant(ZoneOffset.UTC).toString();
    }

    public void setArrivalTime(String localeTime) {
        ZonedDateTime arrivalTime = LocalDateTime.parse(localeTime).atZone(ZoneId.of(arrivalAirport.getTimeZone()));
        arrivalTimeLocale = String.valueOf(arrivalTime);
        arrivalTimeUTC = arrivalTime.withZoneSameInstant(ZoneOffset.UTC).toString();
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", departureAirport=" + departureAirport +
                ", arrivalAirport=" + arrivalAirport +
                '}';
    }
}
