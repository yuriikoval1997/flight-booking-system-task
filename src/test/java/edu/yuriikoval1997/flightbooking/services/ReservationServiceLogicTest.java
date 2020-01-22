package edu.yuriikoval1997.flightbooking.services;

import edu.yuriikoval1997.flightbooking.entities.Aircraft;
import edu.yuriikoval1997.flightbooking.entities.Booking;
import edu.yuriikoval1997.flightbooking.entities.Flight;
import static edu.yuriikoval1997.flightbooking.entities.SeatClass.BUSINESS;
import static edu.yuriikoval1997.flightbooking.entities.SeatPreference.NONE;
import edu.yuriikoval1997.flightbooking.repository.AircraftRepository;
import edu.yuriikoval1997.flightbooking.repository.BookingRepository;
import edu.yuriikoval1997.flightbooking.repository.FlightRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReservationServiceLogicTest {

    private int[][] testSeatPlan;
    private ReservationService reservationService;

    @Mock
    AircraftRepository aircraftRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    FlightRepository flightRepository;

    @BeforeEach
    void init() {
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
                    testSeatPlan[i][j] = 1;
                }
            }
        }
    }

    @BeforeEach
    void setUp() {
        initMocks(this);
        reservationService = new ReservationServiceForAirbusA320(aircraftRepository, flightRepository, bookingRepository);
        when(aircraftRepository.save(any(Aircraft.class))).thenReturn(747L);
        when(flightRepository.save(any(Flight.class))).thenReturn(0L);
        when(bookingRepository.save(any(Booking.class))).thenReturn(0L);
    }

    @DisplayName("Reserve 1 seats in business class, no preference")
    @Test
    void reserveOneSeatsInBusinessClass() {
        testSeatPlan[2][1] = 0;

        assertTrue(reservationService.reserveSeats(1, BUSINESS, NONE, testSeatPlan));
        assertEquals(1, testSeatPlan[2][1]);
    }

    @DisplayName("Reserve 1 seats in business class, aisle preference")
    @Test
    void reserveAisleSeatInBusinessClass() {
        testSeatPlan[2][1] = 0;

        assertTrue(reservationService.reserveSeats(1, BUSINESS, NONE, testSeatPlan));
        assertEquals(1, testSeatPlan[2][1]);
    }

    @DisplayName("Try to reserve 2 seats in business class, aisle preference")
    @Test
    void reserveTwoAisleSeatsInBusinessClass() {
        testSeatPlan[2][1] = 0;

        assertFalse(reservationService.reserveSeats(2, BUSINESS, NONE, testSeatPlan));
        assertEquals(0, testSeatPlan[2][1]);
    }

    @DisplayName("Reserve 2 seats in business class, no preference")
    @Test
    void reserveTwoSeatsInBusinessClass() {
        testSeatPlan[2][0] = 0;
        testSeatPlan[2][1] = 0;

        assertTrue(reservationService.reserveSeats(2, BUSINESS, NONE, testSeatPlan));
        assertEquals(1, testSeatPlan[2][0]);
        assertEquals(1, testSeatPlan[2][1]);
    }

    @DisplayName("Try to reserve 3 seats in business class, no preference")
    @Test
    void reserveThreeSeatsInBusinessClass() {
        testSeatPlan[2][0] = 0;
        testSeatPlan[2][1] = 0;
        testSeatPlan[2][3] = 0;

        assertFalse(reservationService.reserveSeats(3, BUSINESS, NONE, testSeatPlan));
        assertEquals(0, testSeatPlan[2][0]);
        assertEquals(0, testSeatPlan[2][1]);
        assertEquals(0, testSeatPlan[2][3]);
    }

    @DisplayName("Try to reserve 2 seats in economy class, window preference")
    @Test
    void reserveTwoWindowSeat() {
        testSeatPlan[5][0] = 0;
        testSeatPlan[5][1] = 1;
        testSeatPlan[5][2] = 1;

        testSeatPlan[5][4] = 0;
        testSeatPlan[5][5] = 0;
        testSeatPlan[5][6] = 1;

        assertFalse(reservationService.reserveSeats(2, BUSINESS, NONE, testSeatPlan));
        assertEquals(0, testSeatPlan[5][0]);
        assertEquals(1, testSeatPlan[5][1]);
        assertEquals(1, testSeatPlan[5][2]);

        assertEquals(0, testSeatPlan[5][4]);
        assertEquals(0, testSeatPlan[5][5]);
        assertEquals(1, testSeatPlan[5][6]);
    }

    @DisplayName("Try to reserve 2 seats in economy class, aisle preference")
    @Test
    void reserveTwoAisleSeat() {
        testSeatPlan[5][0] = 1;
        testSeatPlan[5][1] = 1;
        testSeatPlan[5][2] = 0;

        testSeatPlan[5][4] = 1;
        testSeatPlan[5][5] = 0;
        testSeatPlan[5][6] = 0;

        assertFalse(reservationService.reserveSeats(2, BUSINESS, NONE, testSeatPlan));
        assertEquals(1, testSeatPlan[5][0]);
        assertEquals(1, testSeatPlan[5][1]);
        assertEquals(0, testSeatPlan[5][2]);

        assertEquals(1, testSeatPlan[5][4]);
        assertEquals(0, testSeatPlan[5][5]);
        assertEquals(0, testSeatPlan[5][6]);
    }
}
