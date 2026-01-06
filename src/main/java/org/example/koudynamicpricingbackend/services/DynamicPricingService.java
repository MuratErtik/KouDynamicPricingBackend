package org.example.koudynamicpricingbackend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.koudynamicpricingbackend.entities.Flight;
import org.example.koudynamicpricingbackend.entities.PriceHistory;
import org.example.koudynamicpricingbackend.entities.SpecialDay;
import org.example.koudynamicpricingbackend.exceptions.FlightException;
import org.example.koudynamicpricingbackend.repositories.FlightRepository;
import org.example.koudynamicpricingbackend.repositories.PriceHistoryRepository;
import org.example.koudynamicpricingbackend.services.PricingCalculator;
import org.example.koudynamicpricingbackend.services.FuzzyInferenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicPricingService {

    private final FlightRepository flightRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final PricingCalculator pricingCalculator;
    private final FuzzyInferenceService fuzzyInferenceService;

    /**
     * single flight update for parameter (
     * - selling ticker or cancel ticker (Trigger: TicketService) not ready yet -> 2nd parameter in .fcl file
     * - change flight time or day (Trigger: FlightService update) -> 4th and 5th parameters in .fcl file
     * - when a day pass through (Trigger: Scheduled Job) not ready yet -> 1st parameter in .fcl file
     */
    @Transactional
    public void updatePriceForFlight(Long flightId, String triggerReason) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightException("Flight not found: " + flightId));

        int daysLeft = pricingCalculator.calculateDaysLeft(flight.getDepartureTime());
        double occupancyRate = pricingCalculator.calculateOccupancyRate(flight);
        double seasonality = pricingCalculator.calculateSeasonalityScore(flight);
        double dayScore = pricingCalculator.calculateDayScore(flight.getDepartureTime());
        double timeScore = pricingCalculator.calculateTimeScore(flight.getDepartureTime());

        // Debug Logs
//        System.out.println("-------------------------0-------------------------------------");
//        System.out.println("daysLeft: " + daysLeft);
//        System.out.println("occupancyRate: " + occupancyRate);
//        System.out.println("seasonality: " + seasonality);
//        System.out.println("dayScore: " + dayScore);
//        System.out.println("timeScore: " + timeScore);
//        System.out.println("-------------------------0-------------------------------------");

        // 2. Fuzzy Logic Calculator
        double multiplier = fuzzyInferenceService.calculatePriceFactor(
                daysLeft, occupancyRate, seasonality, dayScore, timeScore
        );

        System.out.println("Fuzzy Result -> " + multiplier);

        // 3. price calculator
        BigDecimal oldPrice = flight.getCurrentPrice();
        if (oldPrice == null) {
            oldPrice = flight.getBasePrice();
        }

        //  calculate new price
        BigDecimal newPrice = flight.getBasePrice().multiply(BigDecimal.valueOf(multiplier));




        boolean isPriceSame = oldPrice.compareTo(newPrice) == 0;


        boolean isInitial = "Initial Calculation (Create)".equals(triggerReason);

        if (isPriceSame && !isInitial) {
            return;
        }
        // -------------------------------

        System.out.println("=== DEBUG INFO ===");
        System.out.println("Flight: " + flight.getFlightNumber());
        System.out.println("Departure: " + flight.getDepartureTime());
        System.out.println("Days Left: " + daysLeft);
        System.out.println("Occupancy Rate: " + occupancyRate);
        System.out.println("Day Score: " + dayScore);
        System.out.println("Time Score: " + timeScore);
        System.out.println("Price Factor: " + multiplier);
        System.out.println("Final Price: " + newPrice);
        System.out.println("==================");

        // 4. update and save
        flight.setCurrentPrice(newPrice);
        flightRepository.save(flight);

        savePriceHistory(flight, oldPrice, newPrice, multiplier, triggerReason);

        log.info("Flight {} updated. Reason: {}. New Price: {}", flight.getFlightNumber(), triggerReason, newPrice);
    }

    /**
     * Multi Update!
     * it calls when admin adding new special day
     * - and it finds flights which range of that date and country and updates prices.
     */
    @Transactional
    public void updatePricesAffectedBySpecialDay(SpecialDay specialDay) {
        log.info("Special Day Added/Updated: {}. Updating affected flights...", specialDay.getName());

        List<Flight> affectedFlights = flightRepository.findFlightsInDateRange(
                specialDay.getStartDate().atStartOfDay(),
                specialDay.getEndDate().atTime(23, 59, 59)
        );

        int count = 0;
        for (Flight flight : affectedFlights) {
            if (isFlightAffectedByCountry(flight, specialDay)) {
                updatePriceForFlight(flight.getId(), "Special Day Update: " + specialDay.getName());
                count++;
            }
        }
        log.info("Total {} flights updated due to special day.", count);
    }


    private void savePriceHistory(Flight flight, BigDecimal oldPrice, BigDecimal newPrice, double multiplier, String reason) {
        PriceHistory history = PriceHistory.builder()
                .flight(flight)
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .changeTime(LocalDateTime.now())
                .reason(reason)
                .fuzzyMultiplier(multiplier)
                .build();
        priceHistoryRepository.save(history);
    }


    private boolean isFlightAffectedByCountry(Flight flight, SpecialDay specialDay) {
        if (specialDay.getTargetCountry() == null) return true; // if its global must be affected

        String target = specialDay.getTargetCountry();
        return target.equalsIgnoreCase(flight.getDepartureAirport().getCountry()) ||
                target.equalsIgnoreCase(flight.getArrivalAirport().getCountry());
    }
}