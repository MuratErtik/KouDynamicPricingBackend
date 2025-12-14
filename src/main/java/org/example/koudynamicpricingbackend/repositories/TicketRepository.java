package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Flight;
import org.example.koudynamicpricingbackend.entities.Passenger;
import org.example.koudynamicpricingbackend.entities.Seat;
import org.example.koudynamicpricingbackend.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByPnr(String pnr);

    Optional<Ticket> findByPnrAndPassenger(String pnr, Passenger passenger);

    Optional<List<Ticket>> findAllByFlightOrderByIdAsc(Flight flight);

}
