package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.SpecialDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SpecialDayRepository extends JpaRepository<SpecialDay, Long> , JpaSpecificationExecutor<SpecialDay> {
    // Query has three part;
    //first checking country scope, if target country is null it will be global scope like new year
    //second checking date for recurring
    //third if there are any conflict take most pM

    @Query("SELECT s FROM SpecialDay s WHERE " +
            "(s.targetCountry IS NULL OR s.targetCountry = :depCountry OR s.targetCountry = :arrCountry) " +
            "AND " +

            // 2. TARİH KONTROLÜ
            "( " +
            // A) Tekrarlı DEĞİLSE (Örn: Kurban Bayramı 2025): Tam tarih aralığına bak.
            "  (s.isRecurring = false AND :flightDate BETWEEN s.startDate AND s.endDate) " +
            "  OR " +
            // B) Tekrarlı İSE (Örn: Yılbaşı): Yılı yoksay, Ay ve Gün aralığına bak.
            "  (s.isRecurring = true AND " +
            "   (MONTH(:flightDate) * 100 + DAY(:flightDate)) BETWEEN " +
            "   (MONTH(s.startDate) * 100 + DAY(s.startDate)) AND " +
            "   (MONTH(s.endDate) * 100 + DAY(s.endDate)) " +
            "  ) " +
            ") " +

            "ORDER BY s.priceMultiplier DESC LIMIT 1")
    Optional<SpecialDay> findApplicableSpecialDay(
            @Param("flightDate") LocalDate flightDate,
            @Param("depCountry") String depCountry,
            @Param("arrCountry") String arrCountry
    );
}