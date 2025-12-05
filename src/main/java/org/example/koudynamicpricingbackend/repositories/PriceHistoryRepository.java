package org.example.koudynamicpricingbackend.repositories;

import jakarta.transaction.Transactional;
import org.example.koudynamicpricingbackend.entities.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findByFlightIdOrderByChangeTimeAsc(Long flightId);

    @Transactional
    void deleteByFlightId(Long flightId);
}
