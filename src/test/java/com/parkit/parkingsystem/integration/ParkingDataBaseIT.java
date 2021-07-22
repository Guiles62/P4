package com.parkit.parkingsystem.integration;


import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private Date inTime = new Date(2021,07,18);
    private Date outTime = new Date(2021,07,19);


    @Mock
    private  InputReaderUtil inputReaderUtil;




    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO  = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();



    }

    @BeforeEach
    private void setUpPerTest() throws Exception {


        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();



    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        assertEquals(false, ticketDAO.getTicket("ABCDEF").getParkingSpot().isAvailable());
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    }

    @Test
    public void testParkingLotExit() throws InterruptedException {
        testParkingACar();
        Thread.sleep(1000);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        assertEquals(0.0,ticketDAO.getTicket("ABCDEF").getPrice());

        //TODO: check that the fare generated and out time are populated correctly in the database
    }

    @Test

    public void testDiscount() {



        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        inputReaderUtil = null;
        Ticket ticket1 = new Ticket();
        ticket1.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,true));
        ticket1.setId(10);
        ticket1.setVehicleRegNumber("AAAAA");
        ticket1.setInTime(inTime);
        ticket1.setOutTime(null);
        ticket1.setPrice(0);
        ticket1.setDiscount(0);
        parkingSpotDAO.updateParking(ticket1.getParkingSpot());
        ticketDAO.saveTicket(ticket1);
        parkingService.processExitingVehicle();

        Ticket ticket2 = new Ticket();
        ticket2.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,true));
        ticket2.setId(10);
        ticket2.setVehicleRegNumber("AAAAA");
        ticket2.setInTime(new Date(System.currentTimeMillis() - 60 * 60 * 1000));
        ticket2.setOutTime(null);
        ticket2.setPrice(0);
        ticket2.setDiscount(0);
        parkingSpotDAO.updateParking(ticket2.getParkingSpot());
        ticketDAO.saveTicket(ticket2);
        parkingService.processExitingVehicle();

        assertEquals(0.05,ticketDAO.getTicket("AAAAA").getDiscount());
    }

}
