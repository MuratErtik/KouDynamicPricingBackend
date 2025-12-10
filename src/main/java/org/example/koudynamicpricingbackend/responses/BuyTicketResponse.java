package org.example.koudynamicpricingbackend.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BuyTicketResponse {

    private String pnr;

    private String flightNumber;
    private String route;              // like ->Istanbul (IST) -> Ankara (ESB)
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private BigDecimal totalPrice;
    private int passengerCount;

    private List<TicketDetailResponse> tickets;
}

