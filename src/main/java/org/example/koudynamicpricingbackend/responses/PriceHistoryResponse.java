package org.example.koudynamicpricingbackend.responses;

import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PriceHistoryResponse {

    private Long id;

    private BigDecimal oldPrice;

    private BigDecimal newPrice;

    private LocalDateTime changeTime;

    private String reason;

    private Double fuzzyMultiplier;

    private  BigDecimal priceChangePercentage;
}
