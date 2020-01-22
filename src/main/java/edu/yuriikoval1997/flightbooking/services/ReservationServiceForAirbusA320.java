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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReservationServiceForAirbusA320 implements ReservationService {

    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ReservationServiceForAirbusA320(AircraftRepository aircraftRepository,
                                           FlightRepository flightRepository,
                                           BookingRepository bookingRepository) {
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
    }

    // Flight class filtering strategies
    private final Supplier<List<Integer>> getBusinessClassRows =
        () -> IntStream.rangeClosed(0, 3).boxed().collect(Collectors.toList());
    private final Supplier<List<Integer>> getEconomyClassRows =
        () -> IntStream.rangeClosed(4, 38).boxed().collect(Collectors.toList());

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

    private void makeReservation(final int rowIndex, List<Integer> toReserve) {
        toReserve.forEach(seat -> log.info("Seats in row {}, column {} are reserved.%n", rowIndex, seat));

        Aircraft aircraft = new Aircraft("A320", (short) 210);
        Long aircraftId = aircraftRepository.save(aircraft);
        aircraft.setId(aircraftId);

        Flight flight = new Flight(aircraft,
            ZonedDateTime.now(),
            ZonedDateTime.now().plusHours(1),
            "Lviv", "Kyiv");

        Long flightId = flightRepository.save(flight);
        flight.setId(flightId);


        Booking booking = new Booking(flight, 300);
        List<Seat> seats = toReserve.stream()
            .map(seatIndex -> new Seat((short) rowIndex, seatIndex.shortValue(), booking))
            .collect(Collectors.toList());

        booking.setSeats(seats);
        bookingRepository.save(booking);
    }
}
