package org.example.koudynamicpricingbackend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.koudynamicpricingbackend.entities.Flight;
import org.example.koudynamicpricingbackend.repositories.FlightRepository;
import org.example.koudynamicpricingbackend.services.DynamicPricingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NightlyPriceUpdateJob {

    private final FlightRepository flightRepository;
    private final DynamicPricingService dynamicPricingService;


    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Istanbul")
    public void updateFlightPrices() {
        log.info("⏰ Nightly Price Update Job triggered at: {}", LocalDateTime.now());

        try {
            List<Flight> futureFlights = flightRepository.findAllFutureFlights();

            if (futureFlights.isEmpty()) {
                log.info("No future flights found. Skipping update.");
                return;
            }

            log.info("Found {} future flights to update.", futureFlights.size());

            int successCount = 0;
            int errorCount = 0;

            for (Flight flight : futureFlights) {
                try {
                    dynamicPricingService.updatePriceForFlight(flight.getId(), "Nightly Job (Time Decay)");
                    successCount++;
                } catch (Exception e) {
                    log.error("Failed to update price for flight ID {}: {}", flight.getId(), e.getMessage());
                    errorCount++;
                }
            }

            log.info("Nightly Job Finished. Success: {}, Errors: {}", successCount, errorCount);

        } catch (Exception e) {
            log.error("CRITICAL ERROR: Nightly Job crashed entirely!", e);
        }
    }
}
