package org.example.koudynamicpricingbackend.responses;

import lombok.Data;
import org.example.koudynamicpricingbackend.domains.FlightStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FlightResponseForPublic {

    private Long id;

    private String flightNumber;

    private AirportResponse departureAirport;

    private AirportResponse arrivalAirport;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private BigDecimal currentPrice;

    private BigDecimal discountPrice;

}
