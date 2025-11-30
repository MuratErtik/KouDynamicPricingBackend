package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {
}
