package edu.yuriikoval1997.flightbooking.services;

import static edu.yuriikoval1997.flightbooking.entities.SeatClass.BUSINESS;
import static edu.yuriikoval1997.flightbooking.entities.SeatClass.ECONOMY;
import static edu.yuriikoval1997.flightbooking.entities.SeatPreference.*;
import edu.yuriikoval1997.flightbooking.repository.AircraftRepository;
import edu.yuriikoval1997.flightbooking.repository.BookingRepository;
import edu.yuriikoval1997.flightbooking.repository.FlightRepository;
import edu.yuriikoval1997.flightbooking.repository.FlightRepositoryImpl;
import java.util.Arrays;
import org.hibernate.SessionFactory;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
class ReservationServiceForAirbusA320Test {

    private static int[][] testSeatPlan;

    @Autowired
    private ReservationService reservationService;

    @BeforeAll
    static void init() {
        testSeatPlan = new int[39][];
        for (int i = 0; i < 4; i++) {
            testSeatPlan[i] = new int[5];
        }
        for (int i = 4; i < 39; i++) {
            testSeatPlan[i] = new int[7];
        }

        for (int i = 0; i < testSeatPlan.length; i++) {
            int corridor = testSeatPlan[i].length/2;
            for (int j = 0; j < testSeatPlan[i].length; j++) {
                if (j == corridor) {
                    testSeatPlan[i][corridor] = -1;
                } else {
                    testSeatPlan[i][j] = (int) Math.round(Math.random());
                }
            }
        }
        int rowIndex = 0;
        for (int[] row : testSeatPlan) {
            System.out.println(rowIndex + " => " +  Arrays.toString(row));
            rowIndex++;
        }
    }

    @BeforeEach
    void setUp() {
//        reservationService = new ReservationServiceForAirbusA320();
    }

    @Test
    @DisplayName("Reserve 2 seats in the business class")
    void reserveSeatsInBusinessClass() {
        boolean res = reservationService.reserveSeats(2, BUSINESS, NONE, testSeatPlan);
        assertTrue(res);
    }

    @Test
    @DisplayName("Reserve 2 seats in the economy class")
    void reserveSeatsInEconomyClass() {
        boolean res = reservationService.reserveSeats(2, ECONOMY, NONE, testSeatPlan);
        assertTrue(res);
    }
}