package edu.yuriikoval1997.flightbooking.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WindowPreferenceStrategy implements SeatPreferenceStrategy {

    @Override
    public List<Integer> findSuitableRows(int seatsInRow) {
        return List.of(0, seatsInRow - 1);
    }

    @Override
    public List<Integer> findSuitableSeats(int[] row, int seatCount) {
        List<Integer> booked = new ArrayList<>(seatCount);
        int corridor = row.length / 2;
        int i = 0;
        while (i < corridor && booked.size() < seatCount) {
            if (row[i] == 0) {
                booked.add(i);
            } else {
                booked.clear();
            }
            i++;
        }

        if (booked.size() == seatCount) {
            return Collections.unmodifiableList(booked);
        } else {
            booked.clear();
        }

        i = row.length - 1;
        while (i > corridor && booked.size() < seatCount) {
            if (row[i] == 0) {
                booked.add(i);
            } else {
                booked.clear();
            }
            i--;
        }
        if (booked.size() == seatCount) {
            return Collections.unmodifiableList(booked);
        }
        return Collections.emptyList();
    }
}
