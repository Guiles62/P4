package com.parkit.parkingsystem.integration;



import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private final static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;




    @Mock
    private  InputReaderUtil inputReaderUtil;


    @BeforeAll
    private static void setUp() {
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

        Ticket ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,true));
        ticket.setId(0);
        ticket.setVehicleRegNumber("AAAAA");
        ticket.setPrice(0);
        ticket.setInTime(new Date());
        ticket.setOutTime(new Date());
        ticket.setDiscount(0);
        ticketDAO.saveTicket(ticket);






    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        assertFalse( ticketDAO.getTicket("ABCDEF").getParkingSpot().isAvailable());
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
    public void testDiscount() throws Exception {

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("AAAAA");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        Ticket ticket1 = new Ticket();
        ticket1.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,true));
        ticket1.setId(0);
        ticket1.setVehicleRegNumber("AAAAA");
        ticket1.setPrice(0);
        ticket1.setInTime(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        ticket1.setOutTime(new Date(System.currentTimeMillis()));
        ticket1.setDiscount(0);
        ticketDAO.saveTicket(ticket1);
        parkingService.processExitingVehicle();
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();

        BigDecimal bd = new BigDecimal(Fare.CAR_RATE_PER_HOUR * 0.95 * 24).setScale(2, RoundingMode.HALF_UP);
        double roundPrice = bd.doubleValue();

        assertEquals(roundPrice,ticketDAO.getTicket("AAAAA").getPrice());
    }

}
