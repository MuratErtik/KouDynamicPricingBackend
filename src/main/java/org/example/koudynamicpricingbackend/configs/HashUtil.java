package org.example.koudynamicpricingbackend.configs;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class HashUtil {

    private static final String SECRET_PEPPER = "KouAirlines_SuperSecretKey_2025!";

    public String hashIdentityNumber(String rawIdentityNumber) {
        try {

            String input = rawIdentityNumber + SECRET_PEPPER;


            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }
}
