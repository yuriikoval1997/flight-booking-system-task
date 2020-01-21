package edu.yuriikoval1997.flightbooking.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NoPreferenceStrategy implements SeatPreferenceStrategy {

    @Override
    public Stream<Integer> suitableSeats(int seatsInRow) {
        return IntStream.range(0, seatsInRow).boxed();
    }

    @Override
    public List<Integer> findConsecutiveSeats(int[] row, int seatCount) {
        List<Integer> booked = new ArrayList<>(seatCount);
        int i = 0;
        while (i < row.length && booked.size() < seatCount) {
            if (row[i] == 0) {
                booked.add(i);
            } else {
                booked.clear();
            }
            i++;
        }
        if (booked.size() == seatCount) {
            return Collections.unmodifiableList(booked);
        }
        return Collections.emptyList();
    }
}
