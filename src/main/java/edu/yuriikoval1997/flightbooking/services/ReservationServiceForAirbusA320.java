package edu.yuriikoval1997.flightbooking.services;

import static edu.yuriikoval1997.flightbooking.entities.SeatClass.BUSINESS;
import static edu.yuriikoval1997.flightbooking.entities.SeatClass.ECONOMY;
import static edu.yuriikoval1997.flightbooking.entities.SeatPreference.*;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class ReservationServiceForAirbusA320 implements ReservationService {
    private static final IntFunction<List<Integer>> GET_WINDOW_SEATS = seatsInRow -> List.of(0, seatsInRow - 1);
    private static final IntFunction<List<Integer>> GET_AISLE_SEATS = seatsInRow -> List.of(seatsInRow/2 - 1, seatsInRow/2 + 1);
    private static final IntFunction<List<Integer>> NO_PREFERENCE = seatsInRow -> List.of();

    private Map<String, Integer> selectFlightClass(int bookingClass) {
        switch (bookingClass) {
            case BUSINESS: return Map.of("start",0, "end", 4);
            case ECONOMY: return Map.of("start", 4, "end", 39);
            default: throw new IllegalArgumentException();
        }
    }

    private IntFunction<List<Integer>> selectPreferenceStrategy(int bookingPreference) {
        switch (bookingPreference) {
            case NONE: return NO_PREFERENCE;
            case WINDOW: return GET_WINDOW_SEATS;
            case AISLE: return GET_AISLE_SEATS;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean reserveSeats(int seatCount, int bookingClass, int bookingPreference, int[][] seatPlan) {
        var flightClass = selectFlightClass(bookingClass);
        IntFunction<List<Integer>> strategy = selectPreferenceStrategy(bookingPreference);

        //========== temp logging ============
//        Stream.of(seatPlan)
//            .limit(4)
//            .map(Arrays::toString)
//            .forEach(System.out::println);
        //====================================

        // Filter by preference
        List<Integer> list = new ArrayList<>();
        for (int rowIndex = flightClass.get("start"); rowIndex < flightClass.get("end"); rowIndex++) {
            boolean available = false;
            for (int seatIndex : strategy.apply(seatPlan[rowIndex].length)) {
                if (seatPlan[rowIndex][seatIndex] == 0) {
                    available = true;
                    break;
                }
            }
            if (available) {
                list.add(rowIndex);
            }
        }
        // ========================================


        // find consecutive seats
        for (int rowIndex : list) {
            List<Integer> toReserve;
            if (strategy.equals(GET_AISLE_SEATS)) {
                toReserve = iterateFromAisle(seatPlan[rowIndex], seatCount);
            } else {
                toReserve = iterateFromWindow(seatPlan[rowIndex], seatCount);
            }
            if (! toReserve.isEmpty()) {
                makeReservation(rowIndex, toReserve);
                return true;
            }
        }
        return false;
    }

    private void makeReservation(int rowIndex, List<Integer> toReserve) {
        toReserve.forEach(seat -> System.out.printf("Seats in row %d, column %d are reserved.%n", rowIndex, seat));
    }

    private List<Integer> iterateFromWindow(int[] row, int seatCount) {
        List<Integer> booked = new ArrayList<>(seatCount);
        int corridor = row.length / 2;
        int i = 0;
        while (i < corridor && booked.size() <= seatCount) {
            if (row[i] == 0) {
                booked.add(i);
            } else {
                booked.clear();
            }
            i++;
        }

        if (booked.size() == seatCount) {
            return booked;
        } else {
            booked.clear();
        }

        i = row.length - 1;
        while (i > corridor && booked.size() <= seatCount) {
            if (row[i] == 0) {
                booked.add(i);
            } else {
                booked.clear();
            }
            i--;
        }
        if (booked.size() == seatCount) {
            return booked;
        }
        return Collections.emptyList();
    }

    private List<Integer> iterateFromAisle(int[] row, int seatCount) {
        List<Integer> booked = new ArrayList<>(seatCount);
        int corridor = row.length / 2;
        int i = corridor - 1;
        while (i >= 0 && booked.size() <= seatCount) {
            if (row[i] == 0) {
                booked.add(i);
            } else {
                booked.clear();
            }
            i--;
        }

        if (booked.size() == seatCount) {
            return booked;
        } else {
            booked.clear();
        }

        i = corridor + 1;
        while (i < row.length && booked.size() <= seatCount) {
            if (row[i] == 0) {
                booked.add(i);
            } else {
                booked.clear();
            }
            i++;
        }
        if (booked.size() == seatCount) {
            return booked;
        }
        return Collections.emptyList();
    }
}
