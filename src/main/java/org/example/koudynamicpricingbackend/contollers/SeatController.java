package org.example.koudynamicpricingbackend.contollers;


import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.repositories.SeatRepository;
import org.example.koudynamicpricingbackend.responses.SeatResponseForFlight;
import org.example.koudynamicpricingbackend.services.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;


    @GetMapping("/{flightId}")
    public ResponseEntity<List<SeatResponseForFlight>> getSeatsByFlightId(@PathVariable Long flightId) {

        List<SeatResponseForFlight> seats = seatService.getSeatsForFLight(flightId);

        return ResponseEntity.ok(seats);
    }


}
