package edu.yuriikoval1997.flightbooking.repository;

public interface GenericRepository<T> {
    T findById(final Long id);
    Long save(final T entity);
    void delete(final T entity);
    void deleteById(final Long id);
}
