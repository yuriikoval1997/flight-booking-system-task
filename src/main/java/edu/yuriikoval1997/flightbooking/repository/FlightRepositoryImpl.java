package edu.yuriikoval1997.flightbooking.repository;

import edu.yuriikoval1997.flightbooking.entities.Flight;
import org.springframework.stereotype.Repository;

@Repository
public class FlightRepositoryImpl extends GenericRepository<Flight> {

    public FlightRepositoryImpl() {
        super(Flight.class);
    }
}
