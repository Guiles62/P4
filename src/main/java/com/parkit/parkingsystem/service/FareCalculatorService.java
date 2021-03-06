package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());}

        double inHour =  ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        //convert to hours
        double duration = (outHour - inHour) / 3600000 ;

        switch (ticket.getParkingSpot().getParkingType()){


            case CAR: {
                if ( duration > 0.50 ) {
                    double roundDblPrice = Math.round(((duration* Fare.CAR_RATE_PER_HOUR ) - (ticket.getPrice()) * ticket.getDiscount())*100.0)/100.0;
                    ticket.setPrice((roundDblPrice));
                }
                else {
                    ticket.setPrice ( 0 );
                }
                break;
            }
            case BIKE: {
                if ( duration > 0.50 ) {
                    double roundDblPrice = Math.round(((duration* Fare.BIKE_RATE_PER_HOUR ) - (ticket.getPrice()) * ticket.getDiscount())*100.0)/100.0;
                    ticket.setPrice(roundDblPrice);
                }
                else {
                    ticket.setPrice( 0 );
                }
                break;
            }
            default: throw new IllegalArgumentException("Unknow Parking Type");
        }
    }
}