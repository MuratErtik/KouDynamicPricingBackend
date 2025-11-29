package org.example.koudynamicpricingbackend.services;


import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.configs.JwtUtils;
import org.example.koudynamicpricingbackend.domains.UserRole;
import org.example.koudynamicpricingbackend.entities.User;
import org.example.koudynamicpricingbackend.exceptions.AuthException;
import org.example.koudynamicpricingbackend.repositories.UserRepository;
import org.example.koudynamicpricingbackend.requests.SigninRequest;
import org.example.koudynamicpricingbackend.requests.SignupRequest;
import org.example.koudynamicpricingbackend.responses.AuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final EmailService emailService;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";


    public AuthResponse signup(SignupRequest request) throws AuthException, MessagingException {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException("Email already in use");
        }

        User user = new User();

        user.setEmail(request.getEmail());

        user.setName(request.getName());

        user.setLastname(request.getLastname());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setUserRole(UserRole.ROLE_USER);

        user.setMobileNo(request.getMobileNo());

        user.setCreatedAt(LocalDateTime.now());

        user.setLastLoginAt(LocalDateTime.now());

        user.setDateOfBirth(request.getDateOfBirth());

        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getName());

        List<String> roles = new ArrayList<>();

        roles.add(user.getUserRole().toString());

        String accessToken = jwtUtils.generateAccessToken(user.getEmail(),roles);

        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getUserRole().name())
                .message("registered successfully")
                .build();

    }

    public AuthResponse signin(SigninRequest request) {

//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("User not found"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid Password");
        }

        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), List.of(user.getUserRole().name()));
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getUserRole().name())
                .message("Login successful")
                .build();
    }

}
