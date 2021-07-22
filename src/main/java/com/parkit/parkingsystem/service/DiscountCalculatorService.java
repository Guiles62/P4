package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

import java.util.ArrayList;
import java.util.Collections;

public class DiscountCalculatorService {

    private TicketDAO ticketDAO;


    public DiscountCalculatorService (TicketDAO ticketDAO) {

        this.ticketDAO = ticketDAO;
    }
    public void calculateDiscount( Ticket ticket) throws Exception {


        int vehicleNumber = Collections.frequency(ticketDAO.allTicket(ticket.getVehicleRegNumber()), ticket.getVehicleRegNumber());

        if (vehicleNumber > 1)  {
            ticket.setDiscount(0.05);
        } else {
            ticket.setDiscount(0);
        }

    }
}
