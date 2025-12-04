package org.example.koudynamicpricingbackend.services;

import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.entities.SpecialDay;
import org.example.koudynamicpricingbackend.exceptions.SpecialDayException;
import org.example.koudynamicpricingbackend.repositories.SpecialDayRepository;
import org.example.koudynamicpricingbackend.requests.AddSpecialDayRequest;
import org.example.koudynamicpricingbackend.responses.AddSpecialDayResponse;
import org.example.koudynamicpricingbackend.responses.SpecialDayResponse;
import org.example.koudynamicpricingbackend.specifications.SpecialDaySpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialDayService {

    private final SpecialDayRepository specialDayRepository;

    public AddSpecialDayResponse addSpecialDay(AddSpecialDayRequest request) {

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date!");
        }

        String cleanCountry = (request.getTargetCountry() != null) ? request.getTargetCountry().trim() : null;
        String cleanCity = (request.getTargetCity() != null) ? request.getTargetCity().trim() : null;

        SpecialDay specialDay = SpecialDay.builder()
                .name(request.getName().trim()) 
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .priceMultiplier(request.getPriceMultiplier())
                .isRecurring(request.isRecurring())
                .targetCountry(cleanCountry) 
                .targetCity(cleanCity)       
                .build();

        SpecialDay savedDay = specialDayRepository.save(specialDay);

        return getAddSpecialDayResponse(savedDay);
    }

    private AddSpecialDayResponse getAddSpecialDayResponse(SpecialDay savedDay) {
        AddSpecialDayResponse response = new AddSpecialDayResponse();
        response.setMessage("Special day created successfully.");
        response.setId(savedDay.getId());
        response.setName(savedDay.getName());
        response.setStartDate(savedDay.getStartDate());
        response.setEndDate(savedDay.getEndDate());
        response.setPriceMultiplier(savedDay.getPriceMultiplier());
        response.setTargetCountry(savedDay.getTargetCountry());
        response.setRecurring(savedDay.isRecurring());
        response.setTargetCity(savedDay.getTargetCity());
        return response;
    }

    public List<SpecialDayResponse> getAllSpecialDays() {

        List<SpecialDay> days = specialDayRepository.findAll(Sort.by(Sort.Direction.DESC, "startDate"));

        return days.stream()
                .map(this::mapToSpecialDayResponse)
                .collect(Collectors.toList());

    }

    private SpecialDayResponse mapToSpecialDayResponse(SpecialDay savedDay) {
        SpecialDayResponse response = new SpecialDayResponse();
        response.setId(savedDay.getId());
        response.setName(savedDay.getName());
        response.setStartDate(savedDay.getStartDate());
        response.setEndDate(savedDay.getEndDate());

        response.setPriceMultiplier(savedDay.getPriceMultiplier());
        response.setTargetCountry(savedDay.getTargetCountry());
        response.setRecurring(savedDay.isRecurring());
        response.setTargetCity(savedDay.getTargetCity());
        return response;
    }

    public void deleteSpecialDay(Long id) {
        if (!specialDayRepository.existsById(id)) {
            throw new SpecialDayException("Special day not found with id: " + id);
        }
        specialDayRepository.deleteById(id);
    }

    public SpecialDayResponse updateSpecialDay(Long id, AddSpecialDayRequest request) {

        SpecialDay existingDay = specialDayRepository.findById(id)
                .orElseThrow(() -> new SpecialDayException("Special day not found with id: " + id));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new SpecialDayException("End date cannot be before start date!");
        }

        String cleanCountry = (request.getTargetCountry() != null) ? request.getTargetCountry().trim() : null;
        String cleanCity = (request.getTargetCity() != null) ? request.getTargetCity().trim() : null;

        existingDay.setName(request.getName().trim());
        existingDay.setStartDate(request.getStartDate());
        existingDay.setEndDate(request.getEndDate());
        existingDay.setPriceMultiplier(request.getPriceMultiplier());
        existingDay.setRecurring(request.isRecurring());
        existingDay.setTargetCountry(cleanCountry);
        existingDay.setTargetCity(cleanCity);

        SpecialDay updatedDay = specialDayRepository.save(existingDay);

        return mapToSpecialDayResponse(updatedDay);
    }

    public List<SpecialDayResponse> searchSpecialDays(
            String name,
            String targetCountry,
            String targetCity,
            Boolean isRecurring,
            Double minMultiplier,
            Double maxMultiplier,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Specification<SpecialDay> spec = SpecialDaySpecifications.withFilters(
                name, targetCountry, targetCity, isRecurring,
                minMultiplier, maxMultiplier, startDate, endDate
        );

        List<SpecialDay> days = specialDayRepository.findAll(spec);

        return days.stream()
                .map(this::mapToSpecialDayResponse)
                .collect(Collectors.toList());
    }






    // it will be called while flight creating or updating
    //public Double getSeasonalityScore(LocalDate date, Airport departure, Airport arrival)

}
