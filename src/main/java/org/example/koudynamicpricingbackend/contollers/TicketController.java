package org.example.koudynamicpricingbackend.contollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.requests.CreateBookingRequest;
import org.example.koudynamicpricingbackend.responses.BuyTicketResponse;
import org.example.koudynamicpricingbackend.services.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/buy")
    public ResponseEntity<BuyTicketResponse> buyTicket(@RequestBody @Valid CreateBookingRequest request) {

        BuyTicketResponse response = ticketService.buyTicket(request);

        return ResponseEntity.ok(response);
    }
}
