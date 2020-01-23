package edu.yuriikoval1997.flightbooking.repository;

import edu.yuriikoval1997.flightbooking.entities.Booking;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepositoryImpl extends GenericRepository<Booking> {

    public BookingRepositoryImpl() {
        super(Booking.class);
    }
}
