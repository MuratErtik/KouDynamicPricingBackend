package org.example.koudynamicpricingbackend.services;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.example.koudynamicpricingbackend.domains.FlightStatus;
import org.example.koudynamicpricingbackend.domains.SeatStatus;
import org.example.koudynamicpricingbackend.entities.Airport;
import org.example.koudynamicpricingbackend.entities.Flight;
import org.example.koudynamicpricingbackend.entities.Seat;
import org.example.koudynamicpricingbackend.exceptions.AirportException;
import org.example.koudynamicpricingbackend.repositories.AirportRepository;
import org.example.koudynamicpricingbackend.repositories.FlightRepository;
import org.example.koudynamicpricingbackend.repositories.SeatRepository;
import org.example.koudynamicpricingbackend.requests.CreateFlightRequest;
import org.example.koudynamicpricingbackend.responses.CreateFlightResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    private final SeatRepository seatRepository;

    private final AirportRepository airportRepository;

    private final BasePriceService basePriceService;

    private static final Integer FIX_TOTAL_SEATS = 90;

    //CRUD

    @Transactional
    public CreateFlightResponse createFlight(CreateFlightRequest request) {

        Airport departureAirport = airportRepository.findById(request.getDepartureAirportId()).orElseThrow(() -> new AirportException("Airport not found"));

        Airport arrivalAirport = airportRepository.findById(request.getArrivalAirportId()).orElseThrow(() -> new AirportException("Airport not found"));

        String flightNumber = departureAirport.getIataCode()+arrivalAirport.getIataCode()+
                request.getDepartureTime().getYear()+request.getDepartureTime().getMonthValue()+
                request.getDepartureTime().getDayOfMonth()+request.getDepartureTime().getHour()+request.getDepartureTime().getMinute();

        BigDecimal basePrice = basePriceService.calculateBasePrice(departureAirport, arrivalAirport);

        LocalDateTime arrivalTime = basePriceService.calculateArrivalTime(departureAirport,arrivalAirport,request.getDepartureTime());

        Flight flight = Flight.builder()
                .flightNumber(flightNumber)
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .departureTime(request.getDepartureTime())
                .arrivalTime(arrivalTime)
                .basePrice(basePrice)
                .totalSeats(FIX_TOTAL_SEATS)
                .remainingSeats(FIX_TOTAL_SEATS)
                .status(FlightStatus.SCHEDULED)
                .build();

        Flight savedFlight = flightRepository.save(flight);

        generateSeatsForFlight(savedFlight);

        CreateFlightResponse response = new CreateFlightResponse();
        response.setFlightNumber(savedFlight.getFlightNumber());
        response.setId(savedFlight.getId());
        response.setBasePrice(basePrice);
        response.setDepartureCity(departureAirport.getCity());
        response.setDepartureIataCode(departureAirport.getIataCode());
        response.setArrivalCity(arrivalAirport.getCity());
        response.setArrivalIataCode(arrivalAirport.getIataCode());

        response.setDepartureTime(savedFlight.getDepartureTime());

        response.setArrivalTime(arrivalTime);
        response.setTotalSeats(savedFlight.getTotalSeats());
        response.setStatus(FlightStatus.SCHEDULED);

        return response;



    }

    private void generateSeatsForFlight(Flight flight) {

        List<Seat> seats = new ArrayList<>();

        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F'};
        int seatsPerRow = letters.length;

        int totalSeats = FIX_TOTAL_SEATS;
        int rows = (int) Math.ceil((double) totalSeats / seatsPerRow);

        int seatCounter = 0;

        for (int row = 1; row <= rows; row++) {
            for (char letter : letters) {

                if (seatCounter >= totalSeats) break;

                String seatNumber = row + String.valueOf(letter); //"1A"

                Seat seat = Seat.builder()
                        .flight(flight)
                        .seatNumber(seatNumber)
                        .status(SeatStatus.AVAILABLE)
                        .build();

                seats.add(seat);
                seatCounter++;
            }
        }

        seatRepository.saveAll(seats);
    }





}
