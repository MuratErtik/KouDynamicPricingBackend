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
import org.example.koudynamicpricingbackend.requests.CreateBookingRequest;
import org.example.koudynamicpricingbackend.requests.PassengerRequest;
import org.example.koudynamicpricingbackend.requests.SearchWithPnrNumberRequest;
import org.example.koudynamicpricingbackend.responses.BuyTicketResponse;
import org.example.koudynamicpricingbackend.responses.PnrNumberResponse;
import org.example.koudynamicpricingbackend.responses.TicketDetailResponse;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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



    @Transactional
    public BuyTicketResponse buyTicket(CreateBookingRequest createBookingRequest) {

        Flight flight = flightRepository.findById(createBookingRequest.getFlightId())
                .orElseThrow(() -> new FlightException("Flight not found with id: " + createBookingRequest.getFlightId()));

        List<String> selectedSeatsFromPassenger = createBookingRequest.getPassengers().stream()
                .map(PassengerRequest::getSelectedSeatNumber)
                .toList();

        List<Seat> seats = seatRepository.findByFlightAndSeatNumberIn(flight,selectedSeatsFromPassenger)
                .orElseThrow(() -> new SeatException("Seat number not found for flight " + flight.getId()));

        if (seats.size() != selectedSeatsFromPassenger.size()) throw new SeatException("Some selected seats do not exist in this flight!");

        for (Seat seat : seats) {

            if (seat.getStatus() != SeatStatus.AVAILABLE) throw new SeatException("Seat " + seat.getSeatNumber() + " was just taken by another user! Please select another.");

        }

        String pnrCode = generatePNR();

        List<Ticket> createdTickets = new ArrayList<>();

        for(PassengerRequest passengerRequest : createBookingRequest.getPassengers()) {

            Seat seat = seats.stream()
                    .filter(s -> s.getSeatNumber().equals(passengerRequest.getSelectedSeatNumber()))
                    .findFirst()
                    .orElseThrow(() -> new SeatException("Seat number not found for flight " + flight.getId()));


            String hashedIdentity = hashUtil.hashIdentityNumber(passengerRequest.getIdentityNumber());


            Passenger passenger = passengerRepository.findByIdentityNumber(hashedIdentity)
                    .orElseGet(() -> Passenger.builder()
                            .identityNumber(hashedIdentity)
                            .build());

            passenger.setFirstName(passengerRequest.getFirstName());
            passenger.setLastName(passengerRequest.getLastName());
            passenger.setEmail(passengerRequest.getEmail());
            passenger.setPhone(passengerRequest.getPhone());
            passenger.setBirthDate(passengerRequest.getBirthDate());

            passengerRepository.save(passenger);

            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);

            Ticket ticket = Ticket.builder()
                    .pnr(pnrCode)
                    .flight(flight)
                    .passenger(passenger)
                    .seat(seat)
                    .soldPrice(flight.getCurrentPrice())
                    .purchaseDate(LocalDateTime.now())
                    .build();
            ticketRepository.save(ticket);

            flight.setRemainingSeats(flight.getRemainingSeats() - 1);
            createdTickets.add(ticket);



        }

        flightRepository.save(flight);
        dynamicPricingService.updatePriceForFlight(flight.getId(), "Ticket Sold");

        BigDecimal totalAmount = createdTickets.stream()
                .map(Ticket::getSoldPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Flight f = createdTickets.get(0).getFlight();

        List<TicketDetailResponse> detailDtos = createdTickets.stream()
                .map(t -> TicketDetailResponse.builder()
                        .ticketId(t.getId())
                        .seatNumber(t.getSeat().getSeatNumber())
                        .passengerName(t.getPassenger().getFirstName() + " " + t.getPassenger().getLastName())
                        .build())
                .toList();

        if (createBookingRequest.getContactEmail() != null && !createBookingRequest.getContactEmail().isEmpty()) {

            String contactName = createBookingRequest.getPassengers().get(0).getFirstName();

            String flightInfo = f.getDepartureAirport().getCity() + " (" + f.getDepartureAirport().getIataCode() + ") -> " +
                    f.getArrivalAirport().getCity() + " (" + f.getArrivalAirport().getIataCode() + ") on " +
                    f.getDepartureTime().toLocalDate();

            emailService.sendTicketInfoEmail(
                    createBookingRequest.getContactEmail(),
                    contactName,
                    createdTickets.get(0).getPnr(),
                    f,
                    createdTickets,
                    totalAmount
            );
        }

        return BuyTicketResponse.builder()
                .pnr(createdTickets.get(0).getPnr())
                .flightNumber(f.getFlightNumber())
                .route(f.getDepartureAirport().getCity() + " (" + f.getDepartureAirport().getIataCode() + ") -> " +
                        f.getArrivalAirport().getCity() + " (" + f.getArrivalAirport().getIataCode() + ")")
                .departureTime(f.getDepartureTime())
                .arrivalTime(f.getArrivalTime())
                .totalPrice(totalAmount)
                .passengerCount(createdTickets.size())
                .tickets(detailDtos)
                .build();

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
}
