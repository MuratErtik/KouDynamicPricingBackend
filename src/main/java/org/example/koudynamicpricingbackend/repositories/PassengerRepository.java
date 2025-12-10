package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
