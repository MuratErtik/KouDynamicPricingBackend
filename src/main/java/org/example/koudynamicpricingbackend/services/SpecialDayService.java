package org.example.koudynamicpricingbackend.services;

import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.entities.SpecialDay;
import org.example.koudynamicpricingbackend.repositories.SpecialDayRepository;
import org.example.koudynamicpricingbackend.requests.AddSpecialDayRequest;
import org.example.koudynamicpricingbackend.responses.AddSpecialDayResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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






    // it will be called while flight creating or updating
    //public Double getSeasonalityScore(LocalDate date, Airport departure, Airport arrival)

}
