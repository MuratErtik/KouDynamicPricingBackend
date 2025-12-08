package org.example.koudynamicpricingbackend.contollers;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.responses.AirportResponse;
import org.example.koudynamicpricingbackend.services.AirportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/airport")
@RequiredArgsConstructor
public class AirportController {

    private final AirportService airportService;

    @GetMapping("/get-all")
    public ResponseEntity<List<AirportResponse>> getAllAirports() {
        return ResponseEntity.ok(airportService.getAllAirports());
    }

    @GetMapping("/countries")
    public ResponseEntity<List<String>> getCountries() {
        return ResponseEntity.ok(airportService.getAllCountries());
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities(@RequestParam String country) {
        return ResponseEntity.ok(airportService.getCitiesByCountry(country));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AirportResponse>> searchAirports(
            @RequestParam(required = false) String iataCode,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country
    ) {
        return ResponseEntity.ok(
                airportService.searchFlights(
                        iataCode,
                        name,
                        city,
                        country
                )
        );
    }

}
