package org.example.koudynamicpricingbackend.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateFlightRequest {

    @NotNull(message = "Departure airport is required")
    private Long departureAirportId;

    @NotNull(message = "Arrival airport is required")
    private Long arrivalAirportId;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureTime;


}