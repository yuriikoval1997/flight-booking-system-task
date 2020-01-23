package edu.yuriikoval1997.flightbooking.services;

import edu.yuriikoval1997.flightbooking.entities.Aircraft;
import edu.yuriikoval1997.flightbooking.entities.Booking;
import edu.yuriikoval1997.flightbooking.entities.Flight;
import edu.yuriikoval1997.flightbooking.entities.Seat;
import static edu.yuriikoval1997.flightbooking.entities.SeatClass.BUSINESS;
import static edu.yuriikoval1997.flightbooking.entities.SeatClass.ECONOMY;
import static edu.yuriikoval1997.flightbooking.entities.SeatPreference.*;
import java.time.ZonedDateTime;
import edu.yuriikoval1997.flightbooking.repository.CommonRepository;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReservationServiceForAirbusA320 implements ReservationService {
    private final CommonRepository<Aircraft> aircraftRepository;
    private final CommonRepository<Flight> flightRepository;
    private final CommonRepository<Booking> bookingRepository;

    // Flight class filtering strategies
    private final FlightClassStrategy businessClass = new BusinessClassStrategy();
    private final FlightClassStrategy economyClass = new EconomyClassStrategy();

    // Flight preference filtering strategy
    private final SeatPreferenceStrategy noPreference = new NoPreferenceStrategy();
    private final SeatPreferenceStrategy windowPreference = new WindowPreferenceStrategy();
    private final SeatPreferenceStrategy aislePreference = new AislePreferenceStrategy();

    @Autowired
    public ReservationServiceForAirbusA320(CommonRepository<Aircraft> aircraftRepository,
                                           CommonRepository<Flight> flightRepository,
                                           CommonRepository<Booking> bookingRepository) {
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Selects flight class filtering strategy.
     *
     * @param bookingClass - {@link edu.yuriikoval1997.flightbooking.entities.SeatClass}
     * @return - {@link FlightClassStrategy} predicate that filters rows belonging to a specified class.
     */
    private FlightClassStrategy selectFlightClassStrategy(int bookingClass) {
        switch (bookingClass) {
            case BUSINESS: return businessClass;
            case ECONOMY: return economyClass;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Selects seat preference filtering strategy.
     *
     * @param bookingPreference - {@link edu.yuriikoval1997.flightbooking.entities.SeatPreference}
     * @return - {@link SeatPreferenceStrategy} predicate that filters rows belonging to a specified preference.
     */
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
        checkSeatPlan(seatPlan);
        FlightClassStrategy classStrategy = selectFlightClassStrategy(bookingClass);
        SeatPreferenceStrategy preferenceStrategy = selectPreferenceSeatStrategy(bookingPreference);

        return filterByClassAndPreference(seatPlan, classStrategy::belongsToClass, preferenceStrategy::isRowSuitable)
            .flatMap(rowIndex -> Stream.of(preferenceStrategy.findConsecutiveSeats(seatPlan[rowIndex], seatCount))
                .filter(preferenceStrategy::isNotEmpty)
                .map(seatsForBooking -> {
                    makeReservation(rowIndex, seatsForBooking, seatPlan);
                    return true;
                })
            )
            .findAny()
            .orElse(false);
    }

    /**
     * Checks whether a given seat plan is valid.
     *
     * @param seatPlan - a two dimensional array containing a seat plan.
     * @throws IllegalArgumentException if the corridor is not at the center.
     */
    private void checkSeatPlan(int[][] seatPlan) {
        boolean valid = Stream.of(seatPlan).allMatch(row -> row[row.length/2] == -1);
        if (! valid) {
            throw new IllegalArgumentException("The corridor always has to be at the center!");
        }
    }

    /**
     * Returns a {@link Stream<Integer>} of row indices that satisfy booking class and seat preference requirements.
     *
     * @param seatPlan - a two dimensional array containing a seat plan.
     * @param classStrategy - {@link FlightClassStrategy#belongsToClass(int[])}
     * @param preferenceStrategy - {@link SeatPreferenceStrategy#isRowSuitable(int[])}
     * @return - a {@link Stream<Integer>} of row indices that passed through the given predicates.
     */
    private Stream<Integer> filterByClassAndPreference(final int[][] seatPlan,
                                                       final Predicate<int[]> classStrategy,
                                                       final Predicate<int[]> preferenceStrategy) {
        return Stream.iterate(0, i -> i < seatPlan.length, i -> i + 1)
            .flatMap(
                rowIndex -> Stream.of(seatPlan[rowIndex])
                    .filter(classStrategy)
                    .filter(preferenceStrategy)
                    .map(ignored -> rowIndex)
            );
    }

    private void makeReservation(final int rowIndex, List<Integer> toReserve, int[][] seatPlan) {
        toReserve.forEach(seat -> log.info("Seat in row {}, column {} is reserved.", rowIndex, seat));
        toReserve.forEach(seatIndex -> seatPlan[rowIndex][seatIndex] = 1);

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
