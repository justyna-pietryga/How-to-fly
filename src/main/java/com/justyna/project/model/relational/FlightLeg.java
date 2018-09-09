package com.justyna.project.model.relational;

import com.justyna.project.model.other.TimeMode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static com.justyna.project.model.other.TimeMode.LOCAL;

@Getter
@Setter
@Entity
public class FlightLeg {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public FlightLeg() {
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

    @ManyToOne
    @JoinColumn(name = "flight_leg")
    private Flight flight;


    public FlightLeg(Airport departureAirport, Airport arrivalAirport, String departureTime, String arrivalTime, TimeMode timeMode) {
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        setDepartureTime(departureTime, timeMode);
        setArrivalTime(arrivalTime, timeMode);
    }

    private void setDepartureTime(String localeTime, TimeMode timeMode) {
        parseTime(localeTime, timeMode, departureAirport.getTimeZone(), Stage.DEPARTURE);
    }

    private void setArrivalTime(String localeTime, TimeMode timeMode) {
        parseTime(localeTime, timeMode, arrivalAirport.getTimeZone(), Stage.ARRIVAL);
    }

    private void parseTime(String time, TimeMode timeMode, String zone, Enum stage) {
        ZonedDateTime zonedDateTime;
        String timeLocale, timeUTC;
        if (timeMode == LOCAL) {
            zonedDateTime = LocalDateTime.parse(time).atZone(ZoneId.of(zone));
            timeLocale = String.valueOf(zonedDateTime);
            timeUTC = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toString();
        } else {
            zonedDateTime = LocalDateTime.parse(time).atZone(ZoneOffset.UTC);
            timeLocale = zonedDateTime.withZoneSameInstant(ZoneId.of(zone)).toString();
            timeUTC = String.valueOf(zonedDateTime);
        }

        if (stage == Stage.DEPARTURE) {
            setDepartureTimeLocale(timeLocale);
            setDepartureTimeUTC(timeUTC);
        } else {
            setArrivalTimeLocale(timeLocale);
            setArrivalTimeUTC(timeUTC);
        }
    }

    @Override
    public String toString() {
        return "FlightLeg{" +
                "id=" + id +
                ", departureAirport=" + departureAirport +
                ", arrivalAirport=" + arrivalAirport +
                "}\n";
    }

    public enum Stage {
        DEPARTURE, ARRIVAL
    }
}
