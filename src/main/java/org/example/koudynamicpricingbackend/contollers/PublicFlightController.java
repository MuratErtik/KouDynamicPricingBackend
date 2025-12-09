package org.example.koudynamicpricingbackend.contollers;


import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.responses.FlightResponseForPublic;
import org.example.koudynamicpricingbackend.services.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/flights")
@RequiredArgsConstructor
public class PublicFlightController {

    private final FlightService flightService;


    @GetMapping("/search")
    public ResponseEntity<List<FlightResponseForPublic>> searchFlightsForPublic(
            @RequestParam(required = false) String departureAirportIataCode,
            @RequestParam(required = false) String arrivalAirportIataCode,
            @RequestParam(required = false) String departureDate) {

        return ResponseEntity.ok(flightService.searchFlightsForPublic(departureAirportIataCode,arrivalAirportIataCode,departureDate));
    }



}
