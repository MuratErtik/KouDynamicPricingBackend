package org.example.koudynamicpricingbackend.contollers;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.requests.SigninRequest;
import org.example.koudynamicpricingbackend.requests.SignupRequest;
import org.example.koudynamicpricingbackend.responses.AuthResponse;
import org.example.koudynamicpricingbackend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) throws MessagingException {

        AuthResponse authResponse = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody SigninRequest request) {

        AuthResponse response = authService.signin(request);

        return ResponseEntity.ok(response);
    }


//
//    @PostMapping("/refresh")
//    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
//        return ResponseEntity.ok(authService.refreshToken(request));
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
//        authService.logout(request);
//        return ResponseEntity.ok("Logged out successfully");
//    }
}