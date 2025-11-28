package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.domains.UserRole;
import org.example.koudynamicpricingbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByUserRole(UserRole userRole);
}
