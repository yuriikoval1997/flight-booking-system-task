package edu.yuriikoval1997.flightbooking.repository;

import edu.yuriikoval1997.flightbooking.entities.Aircraft;
import org.springframework.stereotype.Repository;

@Repository
public class AircraftRepositoryImpl extends GenericRepository<Aircraft> {

    public AircraftRepositoryImpl() {
        super(Aircraft.class);
    }
}
