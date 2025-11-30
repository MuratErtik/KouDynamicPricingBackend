package org.example.koudynamicpricingbackend.contollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.requests.CreateFlightRequest;
import org.example.koudynamicpricingbackend.responses.CreateFlightResponse;
import org.example.koudynamicpricingbackend.services.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/flights")
@RequiredArgsConstructor
public class AdminFlightController {

    private final FlightService flightService;

    @PostMapping("/add")
    public ResponseEntity<CreateFlightResponse> createFlight(@RequestHeader("Authorization") String jwt,
                                                             @Valid @RequestBody CreateFlightRequest request) {

        CreateFlightResponse response = flightService.createFlight(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}