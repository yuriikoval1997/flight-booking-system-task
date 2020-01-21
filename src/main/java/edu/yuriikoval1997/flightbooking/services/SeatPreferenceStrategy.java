package edu.yuriikoval1997.flightbooking.services;

import java.util.List;

public interface SeatPreferenceStrategy {

    List<Integer> suitableSeats(int seatsInRow);

    List<Integer> findConsecutiveSeats(int[] row, int seatCount);
}
