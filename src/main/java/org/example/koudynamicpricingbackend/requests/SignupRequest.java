package org.example.koudynamicpricingbackend.requests;

import jakarta.persistence.*;
import lombok.Data;
import org.example.koudynamicpricingbackend.domains.UserRole;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SignupRequest {

    private String userRole;

    private String name;

    private String lastname;

    private String email;

    private String password;

    private String mobileNo;

    private LocalDate dateOfBirth;

}
