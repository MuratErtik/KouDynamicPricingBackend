package org.example.koudynamicpricingbackend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.koudynamicpricingbackend.domains.FlightStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // Uçuş kodu: örn. TK1234
    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    private Airport arrivalAirport;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    //-------------------

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    //fuzzy price
    @Column(name = "current_price")
    private BigDecimal currentPrice;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats = 90;

    @Column(name = "remaining_seats", nullable = false)
    private Integer remainingSeats;

    @Enumerated(EnumType.STRING)
    private FlightStatus status = FlightStatus.SCHEDULED;





}

