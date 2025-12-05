package org.example.koudynamicpricingbackend.services;

import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.entities.Flight;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class PricingCalculator {

    private final SpecialDayService specialDayService;

    /**
     * FIRST INPUT: Days To Departure
     * FCL EXPECTATION: [0-180] as INT
     */
    public int calculateDaysLeft(LocalDateTime departureTime) {
        long days = ChronoUnit.DAYS.between(LocalDateTime.now(), departureTime);
        // if flight was in past or today than return 0
        return (days < 0) ? 0 : (int) days;
    }

    /**
     * SECOND INPUT: Occupancy Rate
     * FCL EXPECTATION: [0.0 - 1.0] as DOUBLE
     */
    public double calculateOccupancyRate(Flight flight) {
        if (flight.getTotalSeats() == 0) return 0.0;

        double soldSeats = flight.getTotalSeats() - flight.getRemainingSeats();
        return soldSeats / (double) flight.getTotalSeats();
    }

    /**
     * THIRD INPUT: Seasonality (Special Day)
     */
    public double calculateSeasonalityScore(Flight flight) {
        return specialDayService.getSeasonalityScore(
                flight.getDepartureTime().toLocalDate(),
                flight.getDepartureAirport(),
                flight.getArrivalAirport()
        );
    }

    /**
     * FOURTH INPUT: Day Score
     * FCL EXPECTATION: 0.0 (Low Demand) - 1.0 (High Demand)
     * Rule: Friday/Sunday high, Tuesday/Wednesday low.
     */
    public double calculateDayScore(LocalDateTime departureTime) {
        DayOfWeek day = departureTime.getDayOfWeek();

        if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SUNDAY) {
            return 1.0;
        }

        //for business flights
        if (day == DayOfWeek.MONDAY) {
            return 0.8;
        }

        // mid demand
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.THURSDAY) {
            return 0.5;
        }

        // low demand
        return 0.2;
    }

    /**
     * FIFTH INPUT: Time Score
     * FCL EXPECTATION: 0.0 (Low hours) - 1.0 (Busy hours)
     */
    public double calculateTimeScore(LocalDateTime departureTime) {
        int hour = departureTime.getHour();

        if (hour >= 6 && hour <= 9) return 1.0;

        if (hour >= 16 && hour <= 19) return 1.0;

        if (hour >= 10 && hour <= 15) return 0.6;

        if (hour >= 20 && hour <= 23) return 0.4;

        return 0.1;
    }


}
