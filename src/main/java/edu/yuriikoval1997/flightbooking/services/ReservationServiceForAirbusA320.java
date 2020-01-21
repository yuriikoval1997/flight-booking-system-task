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
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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
    private final Supplier<Stream<Integer>> getBusinessClassRows = () -> IntStream.rangeClosed(0, 3).boxed();
    private final Supplier<Stream<Integer>> getEconomyClassRows = () -> IntStream.rangeClosed(4, 38).boxed();

    // Flight preference filtering strategy
    private final SeatPreferenceStrategy noPreference = new NoPreferenceStrategy();
    private final SeatPreferenceStrategy windowPreference = new WindowPreferenceStrategy();
    private final SeatPreferenceStrategy aislePreference = new AislePreferenceStrategy();

    private Supplier<Stream<Integer>> selectFlightClassStrategy(int bookingClass) {
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
        Supplier<Stream<Integer>> classStrategy = selectFlightClassStrategy(bookingClass);
        SeatPreferenceStrategy preferenceStrategy = selectPreferenceSeatStrategy(bookingPreference);

        return filterByClassAndPreference(seatPlan, classStrategy, preferenceStrategy::suitableSeats)
            .flatMap(rowIndex -> Stream.of(preferenceStrategy.findConsecutiveSeats(seatPlan[rowIndex], seatCount))
                .filter(preferenceStrategy::isNotEmpty)
                .map(seatsForBooking -> {
                    makeReservation(rowIndex, seatsForBooking);
                    return true;
                })
            )
            .findAny()
            .orElse(false);
    }

    private Stream<Integer> filterByClassAndPreference(final int[][] seatPlan,
                                                       final Supplier<Stream<Integer>> classStrategy,
                                                       final IntFunction<Stream<Integer>> preferenceStrategy) {
        return classStrategy.get()
            .flatMap(rowIndex -> preferenceStrategy.apply(seatPlan[rowIndex].length)
                .filter(seatIndex -> seatPlan[rowIndex][seatIndex] == 0)
                .map(ignored -> rowIndex)
            );
    }

    private void makeReservation(final int rowIndex, List<Integer> toReserve) {
        toReserve.forEach(seat -> log.info("Seat in row {}, column {} is reserved.", rowIndex, seat));

        Aircraft aircraft = new Aircraft("A320", (short) 210);
        Long aircraftId = aircraftRepository.save(aircraft);
        aircraft.setId(aircraftId);

        Flight flight = new Flight(aircraft,
            ZonedDateTime.now(),
            ZonedDateTime.now().plusHours(1),
            "Lviv", "Kyiv");

        Long flightId = flightRepository.save(flight);
        flight.setId(flightId);

        List<Seat> seats = toReserve.stream()
            .map(seatIndex -> new Seat((short) rowIndex, seatIndex.shortValue()))
            .collect(Collectors.toList());

        Booking booking = new Booking(flight, 300, seats);
        bookingRepository.save(booking);
    }
}
