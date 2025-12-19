package org.example.koudynamicpricingbackend.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelTicketRequest {

    @NotBlank
    private String pnr;

    @NotBlank
    private String identityNumber;
}
