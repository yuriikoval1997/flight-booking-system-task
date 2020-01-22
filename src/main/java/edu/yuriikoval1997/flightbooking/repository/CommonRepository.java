package edu.yuriikoval1997.flightbooking.repository;

public interface CommonRepository<T> {
    /**
     * Method returns entity of type <T> by given id
     * @param id of type {@link Long} unique identifier of the entity
     * @return entity of type <T>
     */
    T findById(final Long id);

    /**
     * Method persists entity of type <T> into the database
     * @param entity of type <T>
     * @return {@link Long} id of saved entity
     */
    Long save(final T entity);

    /**
     * Method removes given entity of type <T> from the database
     * @param entity of type <T>
     */
    void delete(final T entity);

    /**
     * Method removes entity of type <T> from the database by given id
     * @param id of type {@link Long} unique identifier of the entity
     */
    void deleteById(final Long id);
}
