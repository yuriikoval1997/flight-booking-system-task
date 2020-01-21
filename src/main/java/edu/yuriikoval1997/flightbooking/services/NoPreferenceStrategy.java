package edu.yuriikoval1997.flightbooking.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NoPreferenceStrategy implements SeatPreferenceStrategy {

    @Override
    public List<Integer> findSuitableRows(int seatsInRow) {
        return IntStream.range(0, seatsInRow)
            .boxed()
            .collect(Collectors.toList());
    }

    @Override
    public List<Integer> findSuitableSeats(int[] row, int seatCount) {
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
