package org.example.koudynamicpricingbackend.contollers;


import lombok.RequiredArgsConstructor;

import org.example.koudynamicpricingbackend.responses.FlightPriceHistoryResponse;
import org.example.koudynamicpricingbackend.services.PriceHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/price-history")
@RequiredArgsConstructor
public class AdminPriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightPriceHistoryResponse> getPriceHistoryByFlightId(@PathVariable Long flightId) {

        FlightPriceHistoryResponse response =  priceHistoryService.getPriceHistoryByFlightId(flightId);

        return ResponseEntity.ok(response);
    }



}
