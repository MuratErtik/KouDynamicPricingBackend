package org.example.koudynamicpricingbackend.responses;


import lombok.Data;
import org.example.koudynamicpricingbackend.domains.SeatStatus;


@Data
public class SeatResponseForFlight {


    private Long id;


    private String seatNumber;


    private SeatStatus status;

}
