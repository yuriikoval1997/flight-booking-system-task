package edu.yuriikoval1997.flightbooking.services;

import static edu.yuriikoval1997.flightbooking.entities.SeatClass.BUSINESS;
import static edu.yuriikoval1997.flightbooking.entities.SeatClass.ECONOMY;
import static edu.yuriikoval1997.flightbooking.entities.SeatPreference.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReservationServiceForAirbusA320 implements ReservationService {

    // Flight class filtering strategies
    private final Supplier<List<Integer>> getBusinessClassRows =
        () -> IntStream.range(0, 4).boxed().collect(Collectors.toList());
    private final Supplier<List<Integer>> getEconomyClassRows =
        () -> IntStream.range(4, 39).boxed().collect(Collectors.toList());

    // Flight preference filtering strategy
    private final SeatPreferenceStrategy noPreference = new NoPreferenceStrategy();
    private final SeatPreferenceStrategy windowPreference = new WindowPreferenceStrategy();
    private final SeatPreferenceStrategy aislePreference = new AislePreferenceStrategy();

    private Supplier<List<Integer>> selectFlightClassStrategy(int bookingClass) {
        switch (bookingClass) {
            case BUSINESS: return getBusinessClassRows;
            case ECONOMY: return getEconomyClassRows;
            default: throw new IllegalArgumentException();
        }
    }

    private SeatPreferenceStrategy selectPreferenceSeatStrategy(int bookingPreference) {
        switch (bookingPreference) {
            case NONE: return noPreference;
            case WINDOW: return windowPreference;
            case AISLE: return aislePreference;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean reserveSeats(int seatCount, int bookingClass, int bookingPreference, int[][] seatPlan) {
        Supplier<List<Integer>> classStrategy = selectFlightClassStrategy(bookingClass);
        SeatPreferenceStrategy preferenceStrategy = selectPreferenceSeatStrategy(bookingPreference);

        // Filter by preference
        List<Integer> suitableRows = filterByClassAndPreference(seatPlan, classStrategy, preferenceStrategy::suitableSeats);

        // find consecutive seats
        for (int rowIndex : suitableRows) {
            List<Integer> toReserve = preferenceStrategy.findConsecutiveSeats(seatPlan[rowIndex], seatCount);
            if (! toReserve.isEmpty()) {
                makeReservation(rowIndex, toReserve);
                return true;
            }
        }
        return false;
    }

    private List<Integer> filterByClassAndPreference(final int[][] seatPlan,
                                                     final Supplier<List<Integer>> classStrategy,
                                                     final IntFunction<List<Integer>> preferenceStrategy) {
        List<Integer> list = new ArrayList<>();
        for (int rowIndex : classStrategy.get()) {
            boolean available = false;
            for (int seatIndex : preferenceStrategy.apply(seatPlan[rowIndex].length)) {
                if (seatPlan[rowIndex][seatIndex] == 0) {
                    available = true;
                    break;
                }
            }
            if (available) {
                list.add(rowIndex);
            }
        }
        return Collections.unmodifiableList(list);
    }

    private void makeReservation(int rowIndex, List<Integer> toReserve) {
        toReserve.forEach(seat -> log.info("Seats in row {}, column {} are reserved.%n", rowIndex, seat));
    }
}
