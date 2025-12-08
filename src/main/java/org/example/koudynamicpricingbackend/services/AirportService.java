package org.example.koudynamicpricingbackend.services;

import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.entities.Airport;
import org.example.koudynamicpricingbackend.entities.Flight;
import org.example.koudynamicpricingbackend.entities.User;
import org.example.koudynamicpricingbackend.exceptions.AuthException;
import org.example.koudynamicpricingbackend.repositories.AirportRepository;
import org.example.koudynamicpricingbackend.requests.SigninRequest;
import org.example.koudynamicpricingbackend.responses.AirportResponse;
import org.example.koudynamicpricingbackend.responses.AuthResponse;
import org.example.koudynamicpricingbackend.specifications.AirportSpecification;
import org.example.koudynamicpricingbackend.specifications.FlightSpecifications;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;

    public List<AirportResponse> getAllAirports() {

        List<Airport> airports = airportRepository.findAll();

        return airports.stream().map(this::mapToAirportResponse).collect(Collectors.toList());


    }

    private  AirportResponse mapToAirportResponse(Airport airport) {
        AirportResponse response = new AirportResponse();
        response.setId(airport.getId());
        response.setCity(airport.getCity());
        response.setCountry(airport.getCountry());
        response.setIataCode(airport.getIataCode());
        response.setName(airport.getName());
        return response;


    }

    public List<String> getAllCountries() {
        return airportRepository.findAllCountries();
    }

    public List<String> getCitiesByCountry(String country) {
        return airportRepository.findCitiesByCountry(country);
    }


    public List<AirportResponse>  searchFlights(String iataCode, String name, String city, String country) {

        Specification<Airport> spec = (root, query, cb) -> cb.conjunction();

        if (iataCode != null)
            spec = spec.and(AirportSpecification.hasIataCode(iataCode));

        if (name != null)
            spec = spec.and(AirportSpecification.hasName(name));

        if (city != null)
            spec = spec.and(AirportSpecification.hasCity(city));

        if (country != null)
            spec = spec.and(AirportSpecification.hasCountry(country));

        return airportRepository.findAll(spec)
                .stream()
                .map(this::mapToAirportResponse).collect(Collectors.toList());
    }
}
