package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
