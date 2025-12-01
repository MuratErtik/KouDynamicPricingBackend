package org.example.koudynamicpricingbackend.requests;

import jakarta.validation.constraints.Future;
import lombok.Data;
import org.example.koudynamicpricingbackend.domains.FlightStatus;

import java.time.LocalDateTime;

@Data
public class UpdateFlightRequest {


    @Future(message = "New departure time must be in the future")
    private LocalDateTime newDepartureTime;
    private FlightStatus status;
}
