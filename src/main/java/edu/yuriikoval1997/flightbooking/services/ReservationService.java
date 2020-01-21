package edu.yuriikoval1997.flightbooking.services;

public interface ReservationService {

    /**
     * Reserves seats. Makes sure that the seats are consecutive.
     *
     * @param seatCount - the count of the seats to be reserved.
     * @param bookingClass - the class of seats required.
     * @param bookingPreference - the preference of seats required.
     * @param seatPlan - a two dimensional array containing a seat plan.
     * @return true - if contiguous seats are reserved according to preference, false otherwise.
     */
    boolean reserveSeats(int seatCount, int bookingClass, int bookingPreference, int[][] seatPlan);
}
