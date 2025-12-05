package org.example.koudynamicpricingbackend.contollers;

import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.services.FuzzyInferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class TestController {

    private final FuzzyInferenceService fuzzyInferenceService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello World!!!");
    }

    @GetMapping("/fuzzy")
    public ResponseEntity<Map<String, Object>> testFuzzyLogic(
            @RequestParam int days,          //
            @RequestParam double occupancy,  //  (0.0 - 1.0)
            @RequestParam double season,     //
            @RequestParam double day,
            @RequestParam double time
    ) {

        double multiplier = fuzzyInferenceService.calculatePriceFactor(days, occupancy, season, day, time);

        Map<String, Object> response = new HashMap<>();
        response.put("inputs", Map.of(
                "daysToDeparture", days,
                "occupancyRate", occupancy,
                "seasonality", season,
                "dayScore", day,
                "timeScore", time
        ));
        response.put("RESULT_MULTIPLIER", multiplier);
        response.put("MESSAGE", interpretResult(multiplier));

        return ResponseEntity.ok(response);
    }
    private String interpretResult(double multiplier) {
        if (multiplier > 1.5) return "(Extreme Increase)";
        if (multiplier > 1.1) return "(Price Increase)";
        if (multiplier < 0.9) return "(Discount)";
        return "basic price---";
    }
}
