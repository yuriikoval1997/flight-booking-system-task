package edu.yuriikoval1997.flightbooking.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class AislePreferenceStrategy implements SeatPreferenceStrategy {

    @Override
    public Stream<Integer> suitableSeats(int seatsInRow) {
        return Stream.of(seatsInRow/2 - 1, seatsInRow/2 + 1);
    }

    @Override
    public List<Integer> findConsecutiveSeats(int[] row, int seatCount) {
        int corridor = row.length / 2;
        return Optional.of(searchLeft(row, seatCount, corridor))
            .filter(this::isNotEmpty)
            .orElse(searchRight(row, seatCount, corridor));
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
        if (forBooking.contains(row.length/2 - 1) && forBooking.size() == seatCount) {
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
        if (forBooking.contains(row.length/2 + 1) && forBooking.size() == seatCount) {
            return Collections.unmodifiableList(forBooking);
        }
        return Collections.emptyList();
    }
}
