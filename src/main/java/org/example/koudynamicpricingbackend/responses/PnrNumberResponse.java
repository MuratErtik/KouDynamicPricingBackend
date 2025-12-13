package org.example.koudynamicpricingbackend.responses;

import lombok.Builder;
import lombok.Data;

import org.example.koudynamicpricingbackend.domains.FlightStatus;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PnrNumberResponse {

    private Long id;
//
    private Long flightId;

    private String flightNumber;

 //1

    private Long departureAirportId;

    private String departureAirportIataCode;

    private String departureAirportName;

    private String departureAirportCity;

    private String departureAirportCountry;
//1
    //2
    private Long arrivalAirportId;

    private String arrivalAirportIataCode;

    private String arrivalAirportName;

    private String arrivalAirportCity;

    private String arrivalAirportCountry;

    //2
    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private FlightStatus status;

    private Long seatId;

    private String seatNumber;
//
    private BigDecimal soldPrice;

}
