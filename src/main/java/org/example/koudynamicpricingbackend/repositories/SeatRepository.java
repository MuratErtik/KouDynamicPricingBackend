package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Flight;
import org.example.koudynamicpricingbackend.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByFlightIdOrderByIdAsc(Long flightId);

    Optional<List<Seat>> findAllByFlightIdOrderByIdAsc(Long flight_id);

    void deleteByFlightId(Long flightId);

    Optional<List<Seat>> findByFlightAndSeatNumberIn(Flight flight, List<String> seats);
}
