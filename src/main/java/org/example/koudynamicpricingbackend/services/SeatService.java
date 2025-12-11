package org.example.koudynamicpricingbackend.services;

import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.entities.Seat;
import org.example.koudynamicpricingbackend.repositories.SeatRepository;
import org.example.koudynamicpricingbackend.responses.SeatResponseForFlight;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public List<SeatResponseForFlight> getSeatsForFLight(Long flightId) {

        List<Seat> seats = seatRepository.findByFlightIdOrderByIdAsc(flightId);

        return seats.stream().map(this::mapToSeatResponse).collect(Collectors.toList());


    }

    private SeatResponseForFlight mapToSeatResponse(Seat seat) {
        SeatResponseForFlight seatResponseForFlight = new SeatResponseForFlight();
        seatResponseForFlight.setId(seat.getId());
        seatResponseForFlight.setSeatNumber(seat.getSeatNumber());
        seatResponseForFlight.setStatus(seat.getStatus());
        return seatResponseForFlight;
    }
}
