package org.example.koudynamicpricingbackend.requests;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class SearchWithPnrNumberRequest {

    @NotBlank(message = "PNR code cannot be empty.")
    @Size(min = 6, max = 6, message = "PNR code must be exactly 6 characters.")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "PNR code must contain only uppercase letters and numbers.")
    private String pnr;

    @NotBlank(message = "Identity number cannot be empty.")
    @Pattern(regexp = "^[1-9]{1}[0-9]{10}$", message = "Please enter a valid Identity Number (11 digits).")
    private String identityNumber;

}
