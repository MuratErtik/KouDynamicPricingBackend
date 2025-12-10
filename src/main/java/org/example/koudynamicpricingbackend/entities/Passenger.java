package org.example.koudynamicpricingbackend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.koudynamicpricingbackend.domains.CustomerSegment;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "passengers")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String identityNumber;

    private LocalDate birthDate;

    private String email;

    private String phone;

    //private CustomerSegment customerSegment;


}
