package org.example.koudynamicpricingbackend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "airports")
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    //JFK
    @Column(name = "iata_code", length = 3, nullable = false, unique = true)
    private String iataCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "country", nullable = false)
    private String country;


    // Enlem (Örn: 40.982989)
    @Column(nullable = false)
    private Double latitude;

    // Boylam (Örn: 29.309288)
    @Column(nullable = false)
    private Double longitude;

    // "Europe/Istanbul", "America/New_York"
    //it is required for calculate to flight time and arrival time.
    @Column(name = "time_zone", nullable = false)
    private String timeZone;
}