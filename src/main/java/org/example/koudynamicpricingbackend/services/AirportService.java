package org.example.koudynamicpricingbackend.services;

import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.entities.Airport;
import org.example.koudynamicpricingbackend.entities.User;
import org.example.koudynamicpricingbackend.exceptions.AuthException;
import org.example.koudynamicpricingbackend.repositories.AirportRepository;
import org.example.koudynamicpricingbackend.requests.SigninRequest;
import org.example.koudynamicpricingbackend.responses.AirportResponse;
import org.example.koudynamicpricingbackend.responses.AuthResponse;
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


}
