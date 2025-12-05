package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long>, JpaSpecificationExecutor<Flight> {

    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :start AND :end")
    List<Flight> findFlightsInDateRange(LocalDateTime start, LocalDateTime end);
}
