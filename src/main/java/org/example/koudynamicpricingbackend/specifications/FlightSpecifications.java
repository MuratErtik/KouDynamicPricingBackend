package org.example.koudynamicpricingbackend.specifications;

import jakarta.persistence.criteria.Root;
import org.example.koudynamicpricingbackend.entities.Airport;
import org.example.koudynamicpricingbackend.entities.Flight;
import org.example.koudynamicpricingbackend.domains.FlightStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.time.LocalDateTime;

public class FlightSpecifications {

    // ---------------------------------
    // Airport Join Helpers
    // ---------------------------------

    private static Join<Flight, Airport> joinDepartureAirport(Root<Flight> root) {
        return root.join("departureAirport", JoinType.INNER);
    }

    private static Join<Flight, Airport> joinArrivalAirport(Root<Flight> root) {
        return root.join("arrivalAirport", JoinType.INNER);
    }

    // ---------------------------------
    // A) Flight Info Filters
    // ---------------------------------

    public static Specification<Flight> airline(String airline) {
        return (root, query, cb) -> cb.equal(root.get("flightNumber"), airline); // airline yoksa flightNumber prefix yapılabilir
    }

    public static Specification<Flight> status(String status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), FlightStatus.valueOf(status));
    }

    public static Specification<Flight> basePriceMin(Double min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("basePrice"), min);
    }

    public static Specification<Flight> basePriceMax(Double max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("basePrice"), max);
    }

    // ---------------------------------
    // B) Airport & Route Filters
    // ---------------------------------

    public static Specification<Flight> departureAirportName(String name) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(joinDepartureAirport(root).get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }

    public static Specification<Flight> arrivalAirportName(String name) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(joinArrivalAirport(root).get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }

    public static Specification<Flight> departureAirportIata(String iata) {
        return (root, query, cb) ->
                cb.equal(
                        cb.lower(joinDepartureAirport(root).get("iataCode")),
                        iata.toLowerCase()
                );
    }

    public static Specification<Flight> arrivalAirportIata(String iata) {
        return (root, query, cb) ->
                cb.equal(
                        cb.lower(joinArrivalAirport(root).get("iataCode")),
                        iata.toLowerCase()
                );
    }

    public static Specification<Flight> departureCity(String city) {
        return (root, query, cb) ->
                cb.equal(
                        cb.lower(joinDepartureAirport(root).get("city")),
                        city.toLowerCase()
                );
    }

    public static Specification<Flight> arrivalCity(String city) {
        return (root, query, cb) ->
                cb.equal(
                        cb.lower(joinArrivalAirport(root).get("city")),
                        city.toLowerCase()
                );
    }

    public static Specification<Flight> departureCountry(String country) {
        return (root, query, cb) ->
                cb.equal(
                        cb.lower(joinDepartureAirport(root).get("country")),
                        country.toLowerCase()
                );
    }

    public static Specification<Flight> arrivalCountry(String country) {
        return (root, query, cb) ->
                cb.equal(
                        cb.lower(joinArrivalAirport(root).get("country")),
                        country.toLowerCase()
                );
    }

    // ---------------------------------
    // C) Time Filters
    // ---------------------------------

    public static Specification<Flight> departureTimeAfter(String start) {
        LocalDateTime date = LocalDateTime.parse(start);
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("departureTime"), date);
    }

    public static Specification<Flight> departureTimeBefore(String end) {
        LocalDateTime date = LocalDateTime.parse(end);
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("departureTime"), date);
    }

    public static Specification<Flight> onlyFutureFlights() {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("departureTime"), LocalDateTime.now());
    }
}
