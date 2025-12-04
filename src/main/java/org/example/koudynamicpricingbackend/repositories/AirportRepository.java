package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, Long> {

    Optional<Airport> findAirportById(Long id);


    @Query("SELECT DISTINCT airport.country FROM Airport airport ORDER BY airport.country ASC")
    List<String> findAllCountries();

    @Query("SELECT DISTINCT airport.city FROM Airport airport WHERE (airport.country = :country) ORDER BY airport.city ASC")
    List<String> findCitiesByCountry(@Param("country") String country);
}
