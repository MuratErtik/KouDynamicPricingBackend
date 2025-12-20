package org.example.koudynamicpricingbackend.responses;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomersFromFlightResponse {

    private Long id;

    private String pnr;

    //private Flight flight;

    private TicketPassengerResponse passenger;

    private SeatResponseForTicket seat;

    private BigDecimal soldPrice;

    private LocalDateTime purchaseDate;

    @JsonProperty("isCancelled")
    private boolean isCancelled;


}
