package org.example.koudynamicpricingbackend.services;

import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.entities.Airport;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class BasePriceService {

    private static final int EARTH_RADIUS_KM = 6371;

    public double calculateDistanceKm(Airport dep, Airport arr) {
        double lat1 = dep.getLatitude();
        double lon1 = dep.getLongitude();
        double lat2 = arr.getLatitude();
        double lon2 = arr.getLongitude();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    public BigDecimal calculateBasePrice(Airport dep, Airport arr) {

        double distance = calculateDistanceKm(dep, arr);

        double perKm;
        double fixed;

        if (distance < 800) {
            perKm = 0.10;
            fixed = 30;
        } else if (distance < 2500) {
            perKm = 0.08;
            fixed = 50;
        } else {
            perKm = 0.06;
            fixed = 80;
        }

        double price = distance * perKm + fixed;

        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }

    public LocalDateTime calculateArrivalTime(
            Airport dep, Airport arr, LocalDateTime departureTime) {

        // Haversine distances
        double distance = calculateDistanceKm(dep, arr);

        double avgSpeed = 800;
        double durationHours = distance / avgSpeed;

        ZoneId depZone = ZoneId.of(dep.getTimeZone());
        ZoneId arrZone = ZoneId.of(arr.getTimeZone());

        ZonedDateTime depZdt = departureTime.atZone(depZone);

        ZonedDateTime arrZdt = depZdt.plusMinutes((long)(durationHours * 60))
                .withZoneSameInstant(arrZone);

        return arrZdt.toLocalDateTime();
    }

}

