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

        // HIGH DEMAND: (weekend peak)
        if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SUNDAY) {
            return 0.90; // Maksimum talep
        }

        // MEDIUM-HIGH DEMAND: (weekend travel)
        if (day == DayOfWeek.SATURDAY) {
            return 0.75;
        }

        // MEDIUM DEMAND
        if (day == DayOfWeek.MONDAY || day == DayOfWeek.THURSDAY) {
            return 0.55; // mid-level
        }

        // LOW DEMAND
        if (day == DayOfWeek.TUESDAY || day == DayOfWeek.WEDNESDAY) {
            return 0.20; // min demand
        }

        return 0.50; // Fallback
    }

    /**
     * REVISED: Time Score with MORE AGGRESSIVE pricing differences
     * FCL EXPECTATION: 0.0 (Low hours) - 1.0 (Busy hours)
     */
    public double calculateTimeScore(LocalDateTime departureTime) {
        int hour = departureTime.getHour();

        // PRIME PEAK HOURS (07-09 ve 17-19)
        if ((hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19)) {
            return 1.0;
        }

        // SECONDARY PEAK (06:00, 10:00, 16:00, 20:00)
        if (hour == 6 || hour == 10 || hour == 16 || hour == 20) {
            return 0.85;
        }

        // BUSINESS HOURS (11-15): normal level
        if (hour >= 11 && hour <= 15) {
            return 0.60;
        }

        // EVENING (21-22)
        if (hour >= 21 && hour <= 22) {
            return 0.35;
        }

        // LATE NIGHT (23-01)
        if (hour == 23 || hour == 0 || hour == 1) {
            return 0.15;
        }

        // DEEP NIGHT (02-05):
        return 0.05;
    }


}
