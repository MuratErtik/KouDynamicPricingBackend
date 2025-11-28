package org.example.koudynamicpricingbackend.requests;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SignupRequest {

    // Cannot be null and must contain at least one non-whitespace character
    @NotBlank(message = "Name cannot be empty.")
    private String name;

    @NotBlank(message = "Last name cannot be empty.")
    private String lastname;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email address.") // Checks for @ symbol, valid domain structure, etc.
    private String email;

    // Minimum 6 characters (Regex for uppercase/digits is optional here)
    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters.")
    private String password;

    // Regex for Phone (Digits only, optional leading 0 or +, 10-13 digits)
    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^\\+?[0-9]{10,13}$", message = "Please provide a valid phone number (e.g., +905551234567).")
    private String mobileNo;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.") // Prevents entering a future date
    private LocalDate dateOfBirth;

}
