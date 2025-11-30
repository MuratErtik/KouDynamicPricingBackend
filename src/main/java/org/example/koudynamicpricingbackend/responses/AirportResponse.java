package org.example.koudynamicpricingbackend.responses;


import lombok.*;

@Data
public class AirportResponse {

        private Long id;

        private String iataCode;

        private String name;

        private String city;

        private String country;
    }

