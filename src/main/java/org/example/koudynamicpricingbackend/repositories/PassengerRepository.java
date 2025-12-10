package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    // it going to find id no which hashed already
    Optional<Passenger> findByIdentityNumber(String identityNumber);
}
