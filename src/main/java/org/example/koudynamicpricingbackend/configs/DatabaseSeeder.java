package org.example.koudynamicpricingbackend.configs;

import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.domains.UserRole;
import org.example.koudynamicpricingbackend.entities.User;
import org.example.koudynamicpricingbackend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUserRole(UserRole.ROLE_ADMIN)) {

            User admin = User.builder()
                    .name("Super")
                    .lastname("Admin")
                    .email("admin@kou.edu.tr")
                    .password(passwordEncoder.encode("admin123"))
                    .userRole(UserRole.ROLE_ADMIN)
                    .build();

            userRepository.save(admin);
            System.out.println("DEFAULT ADMIN has been created: admin@kou.edu.tr");
        }
    }
}
