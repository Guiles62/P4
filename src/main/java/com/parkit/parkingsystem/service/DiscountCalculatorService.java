package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class DiscountCalculatorService {

    private TicketDAO ticketDAO;


    public DiscountCalculatorService (TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }
    public void calculateDiscount( Ticket ticket) throws Exception {

        Ticket ticketCheck = ticketDAO.getTicket(ticket.getVehicleRegNumber());
        if ( ticketDAO.allTicket(ticket.getVehicleRegNumber()).contains(ticketCheck) ) {
            ticket.setDiscount(0.05);
        } else {
            ticket.setDiscount(0);
        }

    }
}
