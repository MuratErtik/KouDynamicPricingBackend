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
    public ResponseEntity<List<FlightResponse>> getAllFlights(@RequestHeader("Authorization") String jwt) {
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

    @GetMapping("/search")
    public ResponseEntity<?> searchFlights(
            @RequestParam(required = false) String airline,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double basePriceMin,
            @RequestParam(required = false) Double basePriceMax,

            @RequestParam(required = false) String departureAirportName,
            @RequestParam(required = false) String arrivalAirportName,
            @RequestParam(required = false) String departureAirportIataCode,
            @RequestParam(required = false) String arrivalAirportIataCode,
            @RequestParam(required = false) String departureCity,
            @RequestParam(required = false) String arrivalCity,
            @RequestParam(required = false) String departureCountry,
            @RequestParam(required = false) String arrivalCountry,

            @RequestParam(required = false) String departureTimeStart,
            @RequestParam(required = false) String departureTimeEnd,
            @RequestParam(defaultValue = "false") boolean onlyFutureFlights
    ) {
        return ResponseEntity.ok(
                flightService.searchFlights(
                        airline,
                        status,
                        basePriceMin,
                        basePriceMax,
                        departureAirportName,
                        arrivalAirportName,
                        departureAirportIataCode,
                        arrivalAirportIataCode,
                        departureCity,
                        arrivalCity,
                        departureCountry,
                        arrivalCountry,
                        departureTimeStart,
                        departureTimeEnd,
                        onlyFutureFlights
                )
        );
    }

}