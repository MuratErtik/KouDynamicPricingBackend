package org.example.koudynamicpricingbackend.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailTicketResponse {
    private String passengerName;
    private String seatNumber;
    private String maskedIdentity; // : 12*******90
    private String flightType;
}
