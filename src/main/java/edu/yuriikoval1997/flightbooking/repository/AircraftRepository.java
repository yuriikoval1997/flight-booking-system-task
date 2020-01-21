package edu.yuriikoval1997.flightbooking.repository;

import edu.yuriikoval1997.flightbooking.entities.Aircraft;

public interface AircraftRepository extends GenericRepository<Aircraft>{
    Aircraft findByModel(String model);
}
