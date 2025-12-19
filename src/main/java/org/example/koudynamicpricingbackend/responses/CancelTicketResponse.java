package org.example.koudynamicpricingbackend.responses;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CancelTicketResponse {

    private String pnr;

    private String message;

}
