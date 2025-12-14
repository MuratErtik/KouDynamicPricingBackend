package org.example.koudynamicpricingbackend.responses;


import lombok.Data;
import org.example.koudynamicpricingbackend.entities.Flight;

import java.math.BigDecimal;

import java.util.List;

@Data
public class FlightPriceHistoryResponse {

    private Long flightId;

    private List<PriceHistoryResponse> priceHistories;

    private BigDecimal averagePrice;

}
