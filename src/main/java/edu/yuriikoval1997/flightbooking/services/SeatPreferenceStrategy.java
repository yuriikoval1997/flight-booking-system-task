package edu.yuriikoval1997.flightbooking.services;

import java.util.List;

public interface SeatPreferenceStrategy {

    List<Integer> findSuitableRows(int seatsInRow);

    List<Integer> findSuitableSeats(int[] row, int seatCount);
}
