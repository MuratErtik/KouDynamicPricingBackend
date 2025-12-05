package org.example.koudynamicpricingbackend.services;

import lombok.RequiredArgsConstructor;
import net.sourceforge.jFuzzyLogic.FIS;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FuzzyInferenceService {

    private static final String FCL_FILE_PATH = "fuzzy/pricing.fcl";

    public double calculatePriceFactor(int daysLeft,
                                       double occupancyRate,
                                       double seasonality,
                                       double dayScore,
                                       double timeScore) {

        try {
            ClassPathResource resource = new ClassPathResource(FCL_FILE_PATH);
            InputStream inputStream = resource.getInputStream();


            FIS fis = FIS.load(inputStream, true);

            if (fis == null) {
                System.err.println("Error: FCL file could not find/load -> " + FCL_FILE_PATH);
                return 1.0;
            }

            fis.setVariable("daysToDeparture", daysLeft);
            fis.setVariable("occupancyRate", occupancyRate);
            fis.setVariable("seasonality", seasonality);
            fis.setVariable("dayScore", dayScore);
            fis.setVariable("timeScore", timeScore);

            fis.evaluate();

            double result = fis.getVariable("priceFactor").getValue();

            if (Double.isNaN(result) || result <= 0) {
                return 1.0;
            }

            // Debug Log
             System.out.println("Fuzzy Result -> " + result);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Fuzzy Logic error: " + e.getMessage());
            return 1.0;
        }
    }
}