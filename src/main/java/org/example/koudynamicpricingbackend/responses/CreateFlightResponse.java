package org.example.koudynamicpricingbackend.responses;

import lombok.Builder;
import lombok.Data;
import org.example.koudynamicpricingbackend.domains.FlightStatus;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateFlightResponse {

    private Long id;

    private String flightNumber;
    private BigDecimal basePrice;

    private String departureIataCode; // IST
    private String departureCity;     // Istanbul

    private String arrivalIataCode;   // LHR
    private String arrivalCity;       // London

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;


    private Integer totalSeats;

    private FlightStatus status; // SCHEDULED
}