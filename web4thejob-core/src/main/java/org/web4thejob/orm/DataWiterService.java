package org.web4thejob.orm;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface DataWiterService {

    <E extends Entity> E persist(final E entity);

    public <E extends Entity> E find(Class<E> entityClass, Object primaryKey);

    void remove(Entity entity);
}
