package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {
}
