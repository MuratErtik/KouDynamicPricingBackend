package org.example.koudynamicpricingbackend.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String accessToken;

    private String refreshToken;

    private String message;

    private Long userId;

    private String email;

    private String name;

    private String role;

}
