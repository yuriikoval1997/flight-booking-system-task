package edu.yuriikoval1997.flightbooking.services;

public class EconomyClassStrategy implements FlightClassStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean belongsToClass(int[] row) {
        return row.length == 7;
    }
}
