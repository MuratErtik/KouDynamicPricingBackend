package org.example.koudynamicpricingbackend.responses;


import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketPassengerResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private String email;

    private String phone;
}
