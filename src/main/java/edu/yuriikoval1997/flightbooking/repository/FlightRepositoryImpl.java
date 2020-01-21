package edu.yuriikoval1997.flightbooking.repository;

import edu.yuriikoval1997.flightbooking.constants.ExceptionConstants;
import edu.yuriikoval1997.flightbooking.entities.Flight;
import edu.yuriikoval1997.flightbooking.exceptions.NotFoundException;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FlightRepositoryImpl implements FlightRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public FlightRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Flight findById(final Long flightId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.of(session.get(Flight.class, flightId))
                .orElseThrow(() -> new NotFoundException(ExceptionConstants.FLIGHT_NOT_FOUND));
        }
    }

    @Override
    public Long save(final Flight entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Long id = (Long) session.save(entity);
            session.getTransaction().commit();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(final Flight entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(final Long flightId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Flight flight = session.load(Flight.class, flightId);
            session.delete(flight);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
