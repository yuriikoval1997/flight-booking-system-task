package edu.yuriikoval1997.flightbooking.services;

import static edu.yuriikoval1997.flightbooking.entities.SeatStatus.AVAILABLE;
import java.util.List;

public interface SeatPreferenceStrategy {

    /**
     * Checks whether the given row has at least one {@link edu.yuriikoval1997.flightbooking.entities.SeatStatus#AVAILABLE}
     * seat that satisfy seat preference.
     *
     * @param row - row from the seatPlan {@see ReservationService#reserveSeats(int, int, int, int[][])}
     * @return - {@code true} if the given row has at least one {@link edu.yuriikoval1997.flightbooking.entities.SeatStatus#AVAILABLE}
     * seat that satisfy seat preference.
     */
    boolean isRowSuitable(int[] row);

    /**
     * Searches for seats that are consecutive in one row.
     *
     * @param row - row from the seatPlan {@see ReservationService#reserveSeats(int, int, int, int[][])}
     * @param seatCount - number of seats that have to be consecutive
     * @return - {@link List<Integer>} with consecutive seats indices or empty list if there is not enough seats
     */
    List<Integer> findConsecutiveSeats(int[] row, int seatCount);

    /**
     * Checks whether the given list is not Empty
     *
     * @param list - {@link List}
     * @return {@code true} if the given {@link List} is available
     */
    default boolean isNotEmpty(List<?> list) {
        return ! list.isEmpty();
    }

    /**
     * Checks whether a seat is available.
     *
     * @param seatStatus {@link edu.yuriikoval1997.flightbooking.entities.SeatStatus}
     * @return {@code true} if seat is available.
     */
    default boolean isSeatAvailable(int seatStatus) {
        return seatStatus == AVAILABLE;
    }
}
