package edu.yuriikoval1997.flightbooking.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class NoPreferenceStrategy implements SeatPreferenceStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRowSuitable(int[] row) {
        return IntStream.of(row)
            .anyMatch(this::isSeatAvailable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> findConsecutiveSeats(int[] row, int seatCount) {
        List<Integer> booked = new ArrayList<>(seatCount);
        int i = 0;
        while (i < row.length && booked.size() < seatCount) {
            if (this.isSeatAvailable(row[i])) {
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
