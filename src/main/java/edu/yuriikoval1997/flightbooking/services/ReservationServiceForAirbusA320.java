package edu.yuriikoval1997.flightbooking.services;

import edu.yuriikoval1997.flightbooking.entities.Aircraft;
import edu.yuriikoval1997.flightbooking.entities.Booking;
import edu.yuriikoval1997.flightbooking.entities.Flight;
import edu.yuriikoval1997.flightbooking.entities.Seat;
import static edu.yuriikoval1997.flightbooking.entities.SeatClass.BUSINESS;
import static edu.yuriikoval1997.flightbooking.entities.SeatClass.ECONOMY;
import static edu.yuriikoval1997.flightbooking.entities.SeatPreference.*;
import edu.yuriikoval1997.flightbooking.repository.AircraftRepository;
import edu.yuriikoval1997.flightbooking.repository.BookingRepository;
import edu.yuriikoval1997.flightbooking.repository.FlightRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceForAirbusA320 implements ReservationService {
    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;

    @Autowired
    public ReservationServiceForAirbusA320(BookingRepository bookingRepository,
                                           FlightRepository flightRepository,
                                           AircraftRepository aircraftRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
    }

    // Flight class filtering strategies
    private static final Supplier<List<Integer>> GET_BUSINESS_CLASS_ROWS =
        () -> IntStream.range(0, 4).boxed().collect(Collectors.toList());
    private static final Supplier<List<Integer>> GET_ECONOMY_CLASS_ROWS =
        () -> IntStream.range(4, 39).boxed().collect(Collectors.toList());

    // Seat preference filtering strategies
    private static final IntFunction<List<Integer>> GET_WINDOW_SEATS = seatsInRow -> List.of(0, seatsInRow - 1);
    private static final IntFunction<List<Integer>> GET_AISLE_SEATS = seatsInRow -> List.of(seatsInRow/2 - 1, seatsInRow/2 + 1);
    private static final IntFunction<List<Integer>> NO_PREFERENCE =
        seatsInRow -> IntStream.range(0, seatsInRow)
            .boxed()
            .collect(Collectors.toList());

    private Supplier<List<Integer>> selectFlightClassStrategy(int bookingClass) {
        switch (bookingClass) {
            case BUSINESS: return GET_BUSINESS_CLASS_ROWS;
            case ECONOMY: return GET_ECONOMY_CLASS_ROWS;
            default: throw new IllegalArgumentException();
        }
    }

    private IntFunction<List<Integer>> selectPreferenceSeatStrategy(int bookingPreference) {
        switch (bookingPreference) {
            case NONE: return NO_PREFERENCE;
            case WINDOW: return GET_WINDOW_SEATS;
            case AISLE: return GET_AISLE_SEATS;
            default: throw new IllegalArgumentException();
        }
    }

    private List<Integer> filterByPreference(final int[][] seatPlan,
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean reserveSeats(int seatCount, int bookingClass, int bookingPreference, int[][] seatPlan) {
        Supplier<List<Integer>> classStrategy = selectFlightClassStrategy(bookingClass);
        IntFunction<List<Integer>> preferenceStrategy = selectPreferenceSeatStrategy(bookingPreference);

        // Filter by preference
        List<Integer> preferences = filterByPreference(seatPlan, classStrategy, preferenceStrategy);

        // find consecutive seats
        for (int rowIndex : preferences) {
            List<Integer> toReserve;
            if (preferenceStrategy.equals(GET_AISLE_SEATS)) {
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

        Aircraft aircraft = new Aircraft("A320", (short)210);
        Long aircraftId = aircraftRepository.save(aircraft);
        Aircraft savedAircraft = aircraftRepository.findById(aircraftId);

        Flight flight = new Flight(savedAircraft, ZonedDateTime.now(), ZonedDateTime.now(), "Lviv", "Kyiv");
        Long flightId = flightRepository.save(flight);
        Flight savedFlight = flightRepository.findById(flightId);

        List<Seat> reservedSeats = new ArrayList<>();
        toReserve.forEach(seat -> reservedSeats.add(new Seat((short)rowIndex, seat.shortValue())));

        Booking booking = new Booking(savedFlight, 300, reservedSeats);
        bookingRepository.save(booking);
    }

    private List<Integer> iterateFromWindow(int[] row, int seatCount) {
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

    private List<Integer> iterateFromAisle(int[] row, int seatCount) {
        List<Integer> booked = new ArrayList<>(seatCount);
        int corridor = row.length / 2;
        int i = corridor - 1;
        while (i >= 0 && booked.size() < seatCount) {
            if (row[i] == 0) {
                booked.add(i);
            } else {
                booked.clear();
            }
            i--;
        }

        if (booked.size() == seatCount) {
            return Collections.unmodifiableList(booked);
        } else {
            booked.clear();
        }

        i = corridor + 1;
        while (i < row.length && booked.size() < seatCount) {
            if (row[i] == 0) {
                booked.add(i);
            } else {
                booked.clear();
            }
            i++;
        }
        if (booked.size() == seatCount) {
            return Collections.unmodifiableList(booked);
        }
        return Collections.emptyList();
    }
}
