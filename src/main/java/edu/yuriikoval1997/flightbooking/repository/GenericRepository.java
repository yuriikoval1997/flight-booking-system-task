package edu.yuriikoval1997.flightbooking.repository;

import edu.yuriikoval1997.flightbooking.constants.ExceptionConstants;
import edu.yuriikoval1997.flightbooking.exceptions.NotFoundException;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

abstract class GenericRepository<T> implements CommonRepository<T> {
    private final Class<T> typeParameterClass;

    @Autowired
    private SessionFactory sessionFactory;

    public GenericRepository(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.of(session.get(typeParameterClass, id))
                .orElseThrow(() -> new NotFoundException(ExceptionConstants.AIRCRAFT_NOT_FOUND));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(T entity) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            T entity = session.load(typeParameterClass, id);
            session.delete(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
