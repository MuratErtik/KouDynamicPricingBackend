package org.example.koudynamicpricingbackend.requests;

import lombok.Data;
import java.util.List;

@Data
public class CreateBookingRequest {

    private Long flightId;

    private String contactEmail;

    private List<PassengerRequest> passengers;

//    private PaymentRequest paymentInfo;
}
