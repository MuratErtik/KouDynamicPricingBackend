package org.example.koudynamicpricingbackend.responses;


import lombok.Data;
import org.example.koudynamicpricingbackend.domains.FlightStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FlightResponse {

    private Long id;

    private String flightNumber;

    private AirportResponse departureAirport;

    private AirportResponse arrivalAirport;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private BigDecimal basePrice;

    private Integer totalSeats = 90;

    private Integer remainingSeats;

    private FlightStatus status ;
}
