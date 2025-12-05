package org.example.koudynamicpricingbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "price_history")
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;


    @Column(nullable = false)
    private BigDecimal oldPrice;


    @Column(nullable = false)
    private BigDecimal newPrice;

    @Column(nullable = false)
    private LocalDateTime changeTime;

    private String reason;

    private Double fuzzyMultiplier;
}