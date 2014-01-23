package org.web4thejob.orm;

import org.springframework.stereotype.Repository;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface DataWiterService {

    void persist(Entity entity);
}
