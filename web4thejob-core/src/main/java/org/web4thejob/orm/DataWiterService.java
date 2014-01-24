package org.web4thejob.orm;

import java.util.Collection;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface DataWiterService {

    <E extends Entity> E persist(final E entity);

    <E extends Entity> Collection<E> persist(final Collection<E> entities);

    void remove(Entity entity);

    void remove(final Collection<? extends Entity> entities);

}
