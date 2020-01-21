package edu.yuriikoval1997.flightbooking.services;

import java.util.List;
import java.util.stream.Stream;

public interface SeatPreferenceStrategy {

    Stream<Integer> suitableSeats(int seatsInRow);

    List<Integer> findConsecutiveSeats(int[] row, int seatCount);

    default boolean isNotEmpty(List<Integer> list) {
        return ! list.isEmpty();
    }
}
