package edu.yuriikoval1997.flightbooking.services;

public class BusinessClassStrategy implements FlightClassStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean belongsToClass(int[] row) {
        return row.length == 5;
    }
}
