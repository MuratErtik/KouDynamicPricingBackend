package org.example.koudynamicpricingbackend.requests;

import lombok.Data;
import java.util.List;

@Data
public class CreateBookingRequest {


    private Long outboundFlightId;

    private Long returnFlightId;

    private String contactEmail;

    private List<PassengerRequest> passengers;

    private boolean isRoundTrip;

//    private PaymentRequest paymentInfo;
}
