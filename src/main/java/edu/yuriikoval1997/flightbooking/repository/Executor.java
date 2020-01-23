package edu.yuriikoval1997.flightbooking.repository;

import java.util.function.Function;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Executor {
    private final SessionFactory sessionFactory;

    @Autowired
    public Executor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T executeTrans(Function<Session, T> callback) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.getTransaction().begin();
            T res = callback.apply(session);
            session.getTransaction().commit();
            return res;
        } catch (Exception e) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            throw new HibernateSessionExecutorException(e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public <T> T execute(Function<Session, T> callback) {
        try (Session session = sessionFactory.openSession()) {
            return callback.apply(session);
        }
    }

    public static class HibernateSessionExecutorException extends RuntimeException {

        public HibernateSessionExecutorException(Throwable cause) {
            super(cause);
        }
    }
}
