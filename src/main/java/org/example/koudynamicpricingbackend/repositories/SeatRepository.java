package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByFlightIdOrderByIdAsc(Long flightId);

    void deleteByFlightId(Long flightId);
}
