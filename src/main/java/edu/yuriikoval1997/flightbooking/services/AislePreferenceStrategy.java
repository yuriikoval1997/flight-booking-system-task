package edu.yuriikoval1997.flightbooking.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AislePreferenceStrategy implements SeatPreferenceStrategy {

    @Override
    public List<Integer> suitableSeats(int seatsInRow) {
        return List.of(seatsInRow/2 - 1, seatsInRow/2 + 1);
    }

    @Override
    public List<Integer> findConsecutiveSeats(int[] row, int seatCount) {
        int corridor = row.length / 2;
        List<Integer> left = searchLeft(row, seatCount, corridor);
        if (left.size() == seatCount) {
            return left;
        }
        List<Integer> right = searchRight(row, seatCount, corridor);
        if (right.size() == seatCount) {
            return right;
        }
        return Collections.emptyList();
    }

    private List<Integer> searchLeft(int[] row, int seatCount, int corridor) {
        List<Integer> forBooking = new ArrayList<>(seatCount);
        int i = corridor - 1;
        while (i >= 0 && forBooking.size() < seatCount) {
            if (row[i] == 0) {
                forBooking.add(i);
            } else {
                forBooking.clear();
            }
            i--;
        }
        if (forBooking.contains(row.length/2 - 1)) {
            return Collections.unmodifiableList(forBooking);
        }
        return Collections.emptyList();
    }

    private List<Integer> searchRight(int[] row, int seatCount, int corridor) {
        List<Integer> forBooking = new ArrayList<>(seatCount);
        int i = corridor + 1;
        while (i < row.length && forBooking.size() < seatCount) {
            if (row[i] == 0) {
                forBooking.add(i);
            } else {
                forBooking.clear();
            }
            i++;
        }
        if (forBooking.contains(row.length/2 + 1)) {
            return Collections.unmodifiableList(forBooking);
        }
        return Collections.emptyList();
    }
}
