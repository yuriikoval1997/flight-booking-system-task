package edu.yuriikoval1997.flightbooking.repository;

import edu.yuriikoval1997.flightbooking.constants.ExceptionConstants;
import edu.yuriikoval1997.flightbooking.entities.Booking;
import edu.yuriikoval1997.flightbooking.exceptions.NotFoundException;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepositoryImpl implements BookingRepository{
    private final SessionFactory sessionFactory;

    @Autowired
    public BookingRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Booking findById(final Long bookingId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.of(session.get(Booking.class, bookingId))
                .orElseThrow(() -> new NotFoundException(ExceptionConstants.BOOKING_NOT_FOUND));
        }
    }

    @Override
    public Long save(final Booking booking) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Long id = (Long) session.save(booking);
            session.getTransaction().commit();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(final Booking booking) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(booking);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(final Long bookingId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Booking aircraft = session.load(Booking.class, bookingId);
            session.delete(aircraft);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
