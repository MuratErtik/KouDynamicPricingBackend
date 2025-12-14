package org.example.koudynamicpricingbackend.services;


import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.entities.Flight;
import org.example.koudynamicpricingbackend.entities.PriceHistory;
import org.example.koudynamicpricingbackend.exceptions.FlightException;
import org.example.koudynamicpricingbackend.repositories.FlightRepository;
import org.example.koudynamicpricingbackend.repositories.PriceHistoryRepository;
import org.example.koudynamicpricingbackend.responses.FlightPriceHistoryResponse;
import org.example.koudynamicpricingbackend.responses.PriceHistoryResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;

    private final FlightRepository flightRepository;

    public FlightPriceHistoryResponse getPriceHistoryByFlightId(Long flightId) {

        Flight flight = flightRepository.findById(flightId).orElseThrow(
                () -> new FlightException("Flight not found with id: " + flightId)
        );

        List<PriceHistory> priceHistories = priceHistoryRepository.findAllByFlight(flight).orElseThrow(
                () -> new FlightException("Price history not found with id: " + flightId)
        );


        FlightPriceHistoryResponse flightPriceHistoryResponse = new FlightPriceHistoryResponse();

        flightPriceHistoryResponse.setFlightId(flightId);

        flightPriceHistoryResponse.setPriceHistories(priceHistories.stream().map(this::mapToPriceHistoryResponse).collect(Collectors.toList()));

        BigDecimal average = BigDecimal.valueOf(0);

        for (PriceHistory priceHistory : priceHistories) {
            average = average.add(priceHistory.getNewPrice())      ;
        }

        average = average.divide(BigDecimal.valueOf(priceHistories.size()), 2, RoundingMode.HALF_UP);

        flightPriceHistoryResponse.setAveragePrice(average);

        return flightPriceHistoryResponse;



    }

    private PriceHistoryResponse mapToPriceHistoryResponse(PriceHistory priceHistory) {
        PriceHistoryResponse response = new PriceHistoryResponse();

        response.setId(priceHistory.getId());
        response.setOldPrice(priceHistory.getOldPrice());
        response.setNewPrice(priceHistory.getNewPrice());
        response.setChangeTime(priceHistory.getChangeTime());
        response.setReason(priceHistory.getReason());
        response.setFuzzyMultiplier(priceHistory.getFuzzyMultiplier());
        BigDecimal percentage =(priceHistory.getNewPrice().subtract(priceHistory.getOldPrice())).divide(priceHistory.getOldPrice(),2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        response.setPriceChangePercentage(percentage);
        return response;



    }





}
