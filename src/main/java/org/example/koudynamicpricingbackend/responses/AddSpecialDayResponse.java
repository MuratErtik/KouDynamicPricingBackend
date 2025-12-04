package org.example.koudynamicpricingbackend.responses;


import lombok.Data;

import java.time.LocalDate;

@Data
public class AddSpecialDayResponse {

    private String message;


    private Long id;

    private String name; // "Christmas", "Bayram", "Sömestr"

    private LocalDate startDate;
    private LocalDate endDate;

    private Double priceMultiplier;


    private String targetCountry;

    private String targetCity;

    private boolean isRecurring = false;
}
