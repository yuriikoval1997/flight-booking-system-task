package edu.yuriikoval1997.flightbooking.repository;

import edu.yuriikoval1997.flightbooking.constants.ExceptionConstants;
import edu.yuriikoval1997.flightbooking.entities.Aircraft;
import edu.yuriikoval1997.flightbooking.exceptions.NotFoundException;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AircraftRepositoryImpl implements AircraftRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public AircraftRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Aircraft findById(final Long aircraftId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.of(session.get(Aircraft.class, aircraftId))
                .orElseThrow(() -> new NotFoundException(ExceptionConstants.AIRCRAFT_NOT_FOUND));
        }
    }

    @Override
    public Aircraft findByModel(final String model) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public Long save(final Aircraft aircraft) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Long id = (Long) session.save(aircraft);
            session.getTransaction().commit();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(final Aircraft aircraft) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(aircraft);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(final Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Aircraft aircraft = session.load(Aircraft.class, id);
            session.delete(aircraft);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
