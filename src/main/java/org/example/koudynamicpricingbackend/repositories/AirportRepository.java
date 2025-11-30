package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, Long> {

    Optional<Airport> findAirportById(Long id);
}
