package org.example.koudynamicpricingbackend.contollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.requests.CreateFlightRequest;
import org.example.koudynamicpricingbackend.requests.UpdateFlightRequest;
import org.example.koudynamicpricingbackend.responses.CreateFlightResponse;
import org.example.koudynamicpricingbackend.responses.FlightResponse;
import org.example.koudynamicpricingbackend.services.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<FlightResponse>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlightsForAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponse> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightResponse> updateFlight(@PathVariable Long id, @RequestBody UpdateFlightRequest request) {
        return ResponseEntity.ok(flightService.updateFlight(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}