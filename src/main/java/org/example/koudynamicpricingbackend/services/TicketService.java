package org.example.koudynamicpricingbackend.services;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.configs.HashUtil;
import org.example.koudynamicpricingbackend.domains.FlightStatus;
import org.example.koudynamicpricingbackend.domains.SeatStatus;
import org.example.koudynamicpricingbackend.entities.Flight;
import org.example.koudynamicpricingbackend.entities.Passenger;
import org.example.koudynamicpricingbackend.entities.Seat;
import org.example.koudynamicpricingbackend.entities.Ticket;
import org.example.koudynamicpricingbackend.exceptions.FlightException;
import org.example.koudynamicpricingbackend.exceptions.PassengerException;
import org.example.koudynamicpricingbackend.exceptions.SeatException;
import org.example.koudynamicpricingbackend.repositories.*;
import org.example.koudynamicpricingbackend.requests.CancelTicketRequest;
import org.example.koudynamicpricingbackend.requests.CreateBookingRequest;
import org.example.koudynamicpricingbackend.requests.PassengerRequest;
import org.example.koudynamicpricingbackend.requests.SearchWithPnrNumberRequest;
import org.example.koudynamicpricingbackend.responses.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final FlightRepository flightRepository;

    private final SeatRepository seatRepository;

    private final PassengerRepository passengerRepository;

    private final TicketRepository ticketRepository;

    private final DynamicPricingService dynamicPricingService;

    private final EmailService emailService;

    private final HashUtil hashUtil;

    private static final int PNR_LENGTH = 6;

    private static final String PNR_CHARACTERS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    private static final SecureRandom random = new SecureRandom();

    private final FlightService flightService;



    @Transactional
    public BuyTicketResponse buyTicket(CreateBookingRequest createBookingRequest) {

        Flight outboundFlight = flightRepository.findById(createBookingRequest.getOutboundFlightId())
                .orElseThrow(() -> new FlightException("Outbound flight not found!"));

        Flight returnFlight;
        if (createBookingRequest.getReturnFlightId() != null) {
            returnFlight = flightRepository.findById(createBookingRequest.getReturnFlightId())
                    .orElseThrow(() -> new FlightException("Return flight not found!"));
        } else {
            returnFlight = null;
        }

        boolean isRoundTrip = (returnFlight != null);

        String pnrCode = generatePNR();

        List<Ticket> outboundTickets = processFlightBooking(
                outboundFlight,
                createBookingRequest.getPassengers(),
                pnrCode,
                true, // isOutbound = true
                isRoundTrip
        );

        List<Ticket> allCreatedTickets = new ArrayList<>(outboundTickets);

        if (isRoundTrip) {
            List<Ticket> returnTickets = processFlightBooking(
                    returnFlight,
                    createBookingRequest.getPassengers(),
                    pnrCode,
                    false, // isOutbound = false
                    isRoundTrip
            );
            allCreatedTickets.addAll(returnTickets);
        }

        BigDecimal totalAmount = allCreatedTickets.stream()
                .map(Ticket::getSoldPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TicketDetailResponse> detailDtos = allCreatedTickets.stream()
                .map(t -> {
                    TicketDetailResponse.TicketDetailResponseBuilder builder =
                            TicketDetailResponse.builder()
                                    .ticketId(t.getId())
                                    .seatNumber(t.getSeat().getSeatNumber())
                                    .outboundFlightNumber(outboundFlight.getFlightNumber())
                                    .passengerName(
                                            t.getPassenger().getFirstName() + " " +
                                                    t.getPassenger().getLastName()
                                    );

                    if (returnFlight != null) {
                        builder.returnFlightNumber(returnFlight.getFlightNumber());
                    }

                    return builder.build();
                })
                .toList();


        if (createBookingRequest.getContactEmail() != null && !createBookingRequest.getContactEmail().isEmpty()) {
            String contactName = createBookingRequest.getPassengers().get(0).getFirstName();

            List<EmailTicketResponse> emailTicketList = new ArrayList<>();

            for (PassengerRequest req : createBookingRequest.getPassengers()) {

                String rawId = req.getIdentityNumber();
                String maskedId = rawId.substring(0, 2) + "*******" + rawId.substring(rawId.length() - 2);
                String fullName = req.getFirstName() + " " + req.getLastName();

                emailTicketList.add(EmailTicketResponse.builder()
                        .passengerName(fullName)
                        .seatNumber(req.getOutboundSeatNumber())
                        .maskedIdentity(maskedId)
                        .build());

                if (createBookingRequest.getReturnFlightId() != null) {
                    emailTicketList.add(EmailTicketResponse.builder()
                            .passengerName(fullName)
                            .seatNumber(req.getReturnSeatNumber())
                            .maskedIdentity(maskedId)
                            .build());
                }
            }

            emailService.sendTicketInfoEmail(
                    createBookingRequest.getContactEmail(),
                    contactName,
                    pnrCode,
                    outboundFlight,
                    returnFlight,
                    emailTicketList,
                    totalAmount
            );
        }

        return BuyTicketResponse.builder()
                .pnr(pnrCode)
                .flightNumber(outboundFlight.getFlightNumber())
                .route(outboundFlight.getDepartureAirport().getCity() + " -> " + outboundFlight.getArrivalAirport().getCity())
                .departureTime(outboundFlight.getDepartureTime())
                .arrivalTime(outboundFlight.getArrivalTime())
                .totalPrice(totalAmount)
                .passengerCount(allCreatedTickets.size())
                .tickets(detailDtos)
                .build();
    }


    private List<Ticket> processFlightBooking(Flight flight, List<PassengerRequest> passengerRequests, String pnrCode, boolean isOutbound, boolean isRoundTrip) {

        List<String> requestedSeats = passengerRequests.stream()
                .map(p -> isOutbound ? p.getOutboundSeatNumber() : p.getReturnSeatNumber())
                .toList();

        List<Seat> seats = seatRepository.findByFlightAndSeatNumberIn(flight, requestedSeats)
                .orElseThrow(() -> new SeatException("Seats lookup failed for flight " + flight.getId()));

        if (seats.size() != requestedSeats.size()) {
            throw new SeatException("Some selected seats do not exist in flight " + flight.getFlightNumber());
        }

        for (Seat seat : seats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatException("Seat " + seat.getSeatNumber() + " is already taken on flight " + flight.getFlightNumber());
            }
        }

        List<Ticket> tickets = new ArrayList<>();

        for (PassengerRequest pReq : passengerRequests) {

            String targetSeatNum = isOutbound ? pReq.getOutboundSeatNumber() : pReq.getReturnSeatNumber();
            Seat seat = seats.stream()
                    .filter(s -> s.getSeatNumber().equals(targetSeatNum))
                    .findFirst()
                    .orElseThrow(() -> new SeatException("Seat mismatch logic error."));

            String hashedIdentity = hashUtil.hashIdentityNumber(pReq.getIdentityNumber());

            Passenger passenger = passengerRepository.findByIdentityNumber(hashedIdentity)
                    .orElseGet(() -> Passenger.builder().identityNumber(hashedIdentity).build());

            passenger.setFirstName(pReq.getFirstName());
            passenger.setLastName(pReq.getLastName());
            passenger.setEmail(pReq.getEmail());
            passenger.setPhone(pReq.getPhone());
            passenger.setBirthDate(pReq.getBirthDate());
            passengerRepository.save(passenger);

            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);


            BigDecimal finalPrice = flightService.discountForRoundTrip(flight.getCurrentPrice(), isRoundTrip);
            if (finalPrice==null) {
                finalPrice=flight.getCurrentPrice();
            }

            Ticket ticket = Ticket.builder()
                    .pnr(pnrCode)
                    .flight(flight)
                    .passenger(passenger)
                    .seat(seat)
                    .soldPrice(finalPrice)
                    .purchaseDate(LocalDateTime.now())
                    .build();
            ticketRepository.save(ticket);

            flight.setRemainingSeats(flight.getRemainingSeats() - 1);
            tickets.add(ticket);
        }

        flightRepository.save(flight);
        dynamicPricingService.updatePriceForFlight(flight.getId(), "Ticket Sold");

        return tickets;
    }

    private String generatePNR() {
        StringBuilder sb = new StringBuilder(PNR_LENGTH);
        String pnr = "";
        do {
            sb.setLength(0);
            for (int i = 0; i < PNR_LENGTH; i++) {
                int randomIndex = random.nextInt(PNR_CHARACTERS.length());
                sb.append(PNR_CHARACTERS.charAt(randomIndex));
            }
            pnr = sb.toString();
        } while (ticketRepository.existsByPnr(pnr));

        return pnr;
    }


    public PnrNumberResponse searchWithPnrNumber(@Valid SearchWithPnrNumberRequest request) {

        String hashedIdentity = hashUtil.hashIdentityNumber(request.getIdentityNumber());

        Passenger passenger = passengerRepository.findByIdentityNumber(hashedIdentity).orElseThrow(
                () -> new PassengerException("Passenger not found with identity number -> " + request.getIdentityNumber())
        );

        Ticket ticket = ticketRepository.findByPnrAndPassenger(request.getPnr(),passenger).orElseThrow(
                () -> new SeatException("ticket  not found with pnr number " + request.getPnr())
        );

        return PnrNumberResponse.builder()
                .id(ticket.getId())
                .flightId(ticket.getFlight().getId())
                .flightNumber(ticket.getFlight().getFlightNumber())
                .departureAirportId(ticket.getFlight().getDepartureAirport().getId())
                .departureAirportIataCode(ticket.getFlight().getDepartureAirport().getIataCode())
                .departureAirportName(ticket.getFlight().getDepartureAirport().getName())
                .departureAirportCity(ticket.getFlight().getDepartureAirport().getCity())
                .departureAirportCountry(ticket.getFlight().getDepartureAirport().getCountry())
                .arrivalAirportId(ticket.getFlight().getArrivalAirport().getId())
                .arrivalAirportName(ticket.getFlight().getArrivalAirport().getName())
                .arrivalAirportCity(ticket.getFlight().getArrivalAirport().getCity())
                .arrivalAirportCountry(ticket.getFlight().getArrivalAirport().getCountry())
                .arrivalAirportIataCode(ticket.getFlight().getArrivalAirport().getIataCode())
                .departureTime(ticket.getFlight().getDepartureTime())
                .arrivalTime(ticket.getFlight().getArrivalTime())
                .status(ticket.getFlight().getStatus())
                .seatId(ticket.getSeat().getId())
                .seatNumber(ticket.getSeat().getSeatNumber())
                .soldPrice(ticket.getSoldPrice())
                .build();
    }

    @Transactional
    public CancelTicketResponse cancelTicket(@Valid CancelTicketRequest request) {

        String hashedIdentity = hashUtil.hashIdentityNumber(request.getIdentityNumber());
        Passenger passenger = passengerRepository.findByIdentityNumber(hashedIdentity).orElseThrow(
                () -> new PassengerException("Passenger not found with identity number -> " + request.getIdentityNumber()));


        List<Ticket> tickets = ticketRepository.findTicketsByPnrAndPassenger(request.getPnr(), passenger);

        if (tickets.isEmpty()) {
            throw new SeatException("Tickets not found with PNR number: " + request.getPnr());
        }

        BigDecimal totalRefund = BigDecimal.ZERO;
        boolean anyActionTaken = false;

        for (Ticket ticket : tickets) {

            if (ticket.isCancelled()) continue;

            Flight flight = ticket.getFlight();

            long hoursUntilFlight = java.time.Duration.between(LocalDateTime.now(), flight.getDepartureTime()).toHours();
            if (hoursUntilFlight < 6) {
                throw new FlightException("Cannot cancel ticket. Less than 6 hours left to flight: " + flight.getFlightNumber());
            }

            Seat seat = ticket.getSeat();
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);

            flight.setRemainingSeats(flight.getRemainingSeats() + 1);
            flightRepository.save(flight);

            ticket.setCancelled(true);
            ticketRepository.save(ticket);

            dynamicPricingService.updatePriceForFlight(flight.getId(), "Ticket Cancelled");

            totalRefund = totalRefund.add(ticket.getSoldPrice());
            anyActionTaken = true;
        }

        if (!anyActionTaken) {
            throw new FlightException("All tickets under this PNR are already cancelled.");
        }

        String contactEmail = tickets.get(0).getPassenger().getEmail();
        String contactName = tickets.get(0).getPassenger().getFirstName();

        emailService.sendCancellationEmail(
                contactEmail,
                contactName,
                request.getPnr(),
                totalRefund
        );

        CancelTicketResponse response = new CancelTicketResponse();
        response.setPnr(request.getPnr());
        response.setMessage("Ticket(s) cancelled successfully. Refund amount: " + totalRefund);

        return response;
    }
}
