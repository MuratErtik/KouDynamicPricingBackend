package org.example.koudynamicpricingbackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.example.koudynamicpricingbackend.domains.CustomerSegment;
import org.example.koudynamicpricingbackend.domains.UserRole;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.net.ProtocolFamily;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.ROLE_USER;

    //@Enumerated(EnumType.STRING)
    //private CustomerSegment customerSegment;

    private String name;

    private String lastname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String mobileNo;

    //private boolean isEnabled = false;

    private LocalDate dateOfBirth;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    //private int loyaltyPoints; //email verification


}
