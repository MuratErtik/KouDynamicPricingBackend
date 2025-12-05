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
import org.example.koudynamicpricingbackend.exceptions.FlightException;
import org.example.koudynamicpricingbackend.repositories.AirportRepository;
import org.example.koudynamicpricingbackend.repositories.FlightRepository;
import org.example.koudynamicpricingbackend.repositories.SeatRepository;
import org.example.koudynamicpricingbackend.requests.CreateFlightRequest;
import org.example.koudynamicpricingbackend.requests.UpdateFlightRequest;
import org.example.koudynamicpricingbackend.responses.AirportResponse;
import org.example.koudynamicpricingbackend.responses.CreateFlightResponse;
import org.example.koudynamicpricingbackend.responses.FlightResponse;
import org.example.koudynamicpricingbackend.specifications.FlightSpecifications;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
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

    private static final Integer FIX_TOTAL_SEATS = 60;

    //C+RUD

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

    //all,id,departure,
    public List<FlightResponse> getAllFlightsForAdmin() {
        return flightRepository.findAll()
                .stream()
                .map(this::mapToFlightResponse)
                .toList();
    }

    private  AirportResponse mapToAirportResponse(Airport airport) {
        AirportResponse response = new AirportResponse();
        response.setId(airport.getId());
        response.setCity(airport.getCity());
        response.setCountry(airport.getCountry());
        response.setIataCode(airport.getIataCode());
        response.setName(airport.getName());
        return response;
    }

    private FlightResponse mapToFlightResponse(Flight flight) {
        FlightResponse response = new FlightResponse();
        response.setId(flight.getId());
        response.setFlightNumber(flight.getFlightNumber());
        response.setDepartureAirport(mapToAirportResponse(flight.getDepartureAirport()));
        response.setArrivalAirport(mapToAirportResponse(flight.getArrivalAirport()));
        response.setDepartureTime(flight.getDepartureTime());
        response.setArrivalTime(flight.getArrivalTime());
        response.setBasePrice(flight.getBasePrice());
        response.setTotalSeats(flight.getTotalSeats());
        response.setRemainingSeats(flight.getRemainingSeats());
        response.setStatus(flight.getStatus());
        return response;


    }

    public FlightResponse getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightException("Flight not found with id: " + id));
        return mapToFlightResponse(flight);
    }

    @Transactional
    public FlightResponse updateFlight(Long id, UpdateFlightRequest request) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightException("Flight not found"));

        if (request.getNewDepartureTime() != null) {
            flight.setDepartureTime(request.getNewDepartureTime());
            LocalDateTime arrivalTime = basePriceService.calculateArrivalTime(flight.getDepartureAirport(),flight.getArrivalAirport(),request.getNewDepartureTime());
            flight.setArrivalTime(arrivalTime);
        }

        if (request.getStatus() != null) {
            flight.setStatus(request.getStatus());
        }

        return mapToFlightResponse(flightRepository.save(flight));
    }

    @Transactional
    public void deleteFlight(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new FlightException("Flight not found");
        }

        seatRepository.deleteByFlightId(id);
        flightRepository.deleteById(id);
    }

    public List<FlightResponse> searchFlights(
            String airline,
            String status,
            Double basePriceMin,
            Double basePriceMax,

            String depName,
            String arrName,
            String depIata,
            String arrIata,
            String depCity,
            String arrCity,
            String depCountry,
            String arrCountry,

            String depStart,
            String depEnd,
            boolean onlyFuture
    ) {

        Specification<Flight> spec = (root, query, cb) -> cb.conjunction();
        
        if (airline != null)
            spec = spec.and(FlightSpecifications.airline(airline));

        if (status != null)
            spec = spec.and(FlightSpecifications.status(status));

        if (basePriceMin != null)
            spec = spec.and(FlightSpecifications.basePriceMin(basePriceMin));

        if (basePriceMax != null)
            spec = spec.and(FlightSpecifications.basePriceMax(basePriceMax));

        // Airport Filters
        if (depName != null)
            spec = spec.and(FlightSpecifications.departureAirportName(depName));

        if (arrName != null)
            spec = spec.and(FlightSpecifications.arrivalAirportName(arrName));

        if (depIata != null)
            spec = spec.and(FlightSpecifications.departureAirportIata(depIata));

        if (arrIata != null)
            spec = spec.and(FlightSpecifications.arrivalAirportIata(arrIata));

        if (depCity != null)
            spec = spec.and(FlightSpecifications.departureCity(depCity));

        if (arrCity != null)
            spec = spec.and(FlightSpecifications.arrivalCity(arrCity));

        if (depCountry != null)
            spec = spec.and(FlightSpecifications.departureCountry(depCountry));

        if (arrCountry != null)
            spec = spec.and(FlightSpecifications.arrivalCountry(arrCountry));

        // Time filters
        if (depStart != null)
            spec = spec.and(FlightSpecifications.departureTimeAfter(depStart));

        if (depEnd != null)
            spec = spec.and(FlightSpecifications.departureTimeBefore(depEnd));

        if (onlyFuture)
            spec = spec.and(FlightSpecifications.onlyFutureFlights());

        return flightRepository.findAll(spec)
                .stream()
                .map(this::mapToFlightResponse)
                .toList();
    }




}
