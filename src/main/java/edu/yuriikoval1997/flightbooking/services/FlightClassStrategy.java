package edu.yuriikoval1997.flightbooking.services;

public interface FlightClassStrategy {

    /**
     * Checks whether a given row belong to a belong to flight class.
     *
     * @param row - row from the seatPlan {@see ReservationService#reserveSeats(int, int, int, int[][])}
     * @return - {@code true} if the given row belong to the flight class
     */
    boolean belongsToClass(int[] row);
}
