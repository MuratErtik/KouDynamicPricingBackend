package org.example.koudynamicpricingbackend.repositories;

import org.example.koudynamicpricingbackend.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
