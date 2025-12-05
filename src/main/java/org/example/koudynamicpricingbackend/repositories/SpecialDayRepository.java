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
            "( " +
            //  Not Recurring
            "  (s.isRecurring = false AND cast(:flightDate as date) BETWEEN s.startDate AND s.endDate) " +
            "  OR " +
            //  Recurring
            "  (s.isRecurring = true AND " +
            "   (EXTRACT(MONTH FROM cast(:flightDate as date)) * 100 + EXTRACT(DAY FROM cast(:flightDate as date))) BETWEEN " +
            "   (EXTRACT(MONTH FROM s.startDate) * 100 + EXTRACT(DAY FROM s.startDate)) AND " +
            "   (EXTRACT(MONTH FROM s.endDate) * 100 + EXTRACT(DAY FROM s.endDate)) " +
            "  ) " +
            ") " +
            "ORDER BY s.priceMultiplier DESC LIMIT 1")
    Optional<SpecialDay> findApplicableSpecialDay(
            @Param("flightDate") LocalDate flightDate,
            @Param("depCountry") String depCountry,
            @Param("arrCountry") String arrCountry
    );
}