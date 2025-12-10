package org.example.koudynamicpricingbackend.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PassengerRequest {

    @NotBlank(message = "First name cannot be empty.")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty.")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.")
    private String lastName;

    @NotBlank(message = "Identity number cannot be empty.")
    @Pattern(regexp = "^[1-9]{1}[0-9]{10}$", message = "Please enter a valid Identity Number (11 digits).")
    private String identityNumber;

    @NotNull(message = "Birth date cannot be empty.")
    @Past(message = "Birth date must be in the past.")
    private LocalDate birthDate;

    @Email(message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Phone number cannot be empty.")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Please enter a valid phone number.")
    private String phone;
}