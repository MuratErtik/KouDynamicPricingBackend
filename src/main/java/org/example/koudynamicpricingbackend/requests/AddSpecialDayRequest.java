package org.example.koudynamicpricingbackend.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddSpecialDayRequest {

    @NotBlank(message = "Special day name cannot be empty")
    @Size(max = 100, message = "Name is too long")
    private String name; // "Christmas", "Bayram", "Sömestr"

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Price multiplier is required")
    @Positive(message = "Price multiplier must be positive")
    @Min(value = 0, message = "Multiplier cannot be negative")
    private Double priceMultiplier;


    private String targetCountry;

    private String targetCity;

    private boolean isRecurring;
}