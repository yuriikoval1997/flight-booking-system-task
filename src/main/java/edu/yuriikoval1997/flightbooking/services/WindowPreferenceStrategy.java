package edu.yuriikoval1997.flightbooking.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class WindowPreferenceStrategy implements SeatPreferenceStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRowSuitable(int[] row) {
        return IntStream.of(row[0], row[row.length - 1])
            .anyMatch(this::isSeatAvailable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> findConsecutiveSeats(int[] row, int seatCount) {
        int corridor = row.length / 2;
        return Optional.of(searchLeft(row, seatCount, corridor))
            .filter(this::isNotEmpty)
            .orElse(searchRight(row, seatCount, corridor));
    }

    private List<Integer> searchLeft(int[] row, int seatCount, int corridor) {
        List<Integer> forBooking = new ArrayList<>(seatCount);
        int i = 0;
        while (i < corridor && forBooking.size() < seatCount) {
            if (this.isSeatAvailable(row[i])) {
                forBooking.add(i);
            } else {
                forBooking.clear();
            }
            i++;
        }
        if (forBooking.contains(0) && forBooking.size() == seatCount) {
            return Collections.unmodifiableList(forBooking);
        }
        return Collections.emptyList();
    }

    private List<Integer> searchRight(int[] row, int seatCount, int corridor) {
        List<Integer> forBooking = new ArrayList<>();
        int i = row.length - 1;
        while (i > corridor && forBooking.size() < seatCount) {
            if (this.isSeatAvailable(row[i])) {
                forBooking.add(i);
            } else {
                forBooking.clear();
            }
            i--;
        }
        if (forBooking.contains(row.length - 1) && forBooking.size() == seatCount) {
            return Collections.unmodifiableList(forBooking);
        }
        return Collections.emptyList();
    }
}
