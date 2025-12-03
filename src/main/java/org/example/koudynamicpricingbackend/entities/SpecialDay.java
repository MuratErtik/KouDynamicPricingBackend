package org.example.koudynamicpricingbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "special_days")
public class SpecialDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // "Christmas", "Bayram", "Sömestr"

    private LocalDate startDate;
    private LocalDate endDate;

    private Double priceMultiplier;

    // TARGET COUNTRY — ör: "Germany", "Turkey", "France"
    @Column(name = "target_country")
    private String targetCountry;

    @Column(name = "target_city")
    private String targetCity;

    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring = false;
}
