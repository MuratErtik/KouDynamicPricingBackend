package org.example.koudynamicpricingbackend.specifications;

import org.example.koudynamicpricingbackend.entities.Airport;
import org.springframework.data.jpa.domain.Specification;

public class AirportSpecification {

    public static Specification<Airport> hasIataCode(String iataCode) {
        return (root, query, cb) -> {
            if (iataCode == null || iataCode.isEmpty()) return cb.conjunction();
            return cb.like(cb.lower(root.get("iataCode")), "%" + iataCode.toLowerCase() + "%");
        };
    }

    public static Specification<Airport> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) return cb.conjunction();
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Airport> hasCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isEmpty()) return cb.conjunction();
            return cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    public static Specification<Airport> hasCountry(String country) {
        return (root, query, cb) -> {
            if (country == null || country.isEmpty()) return cb.conjunction();
            return cb.like(cb.lower(root.get("country")), "%" + country.toLowerCase() + "%");
        };
    }
}