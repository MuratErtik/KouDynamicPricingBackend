package org.example.koudynamicpricingbackend.responses;

import lombok.Data;

@Data
public class AuthResponse {

    private String accessToken;

    private String refreshToken;

    private String message;

    private Long userId;

    private String email;

    private String name;

    private String role;

}
