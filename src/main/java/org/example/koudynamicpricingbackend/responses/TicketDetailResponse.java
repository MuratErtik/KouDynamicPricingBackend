package org.example.koudynamicpricingbackend.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDetailResponse {

    private Long ticketId;
    private String seatNumber;
    private String passengerName;
    private String passengerLastName;

}
