package org.example.koudynamicpricingbackend.specifications;

import jakarta.persistence.criteria.Predicate;
import org.example.koudynamicpricingbackend.entities.SpecialDay;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SpecialDaySpecifications {

    public static Specification<SpecialDay> withFilters(
            String name,
            String targetCountry,
            String targetCity,
            Boolean isRecurring,
            Double minMultiplier,
            Double maxMultiplier,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (targetCountry != null && !targetCountry.isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("targetCountry")), targetCountry.toLowerCase()));
            }

            if (targetCity != null && !targetCity.isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("targetCity")), targetCity.toLowerCase()));
            }

            if (isRecurring != null) {
                predicates.add(cb.equal(root.get("isRecurring"), isRecurring));
            }

            if (minMultiplier != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("priceMultiplier"), minMultiplier));
            }
            if (maxMultiplier != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("priceMultiplier"), maxMultiplier));
            }


            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}