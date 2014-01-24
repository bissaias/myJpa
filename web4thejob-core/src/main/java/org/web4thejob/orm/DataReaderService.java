package org.web4thejob.orm;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public interface DataReaderService {

    <E extends Entity> E find(Class<E> entityClass, Object primaryKey);

    <E extends Entity> CriteriaQuery<E> getCriteriaQuery(Class<E> entityClass);

    <E extends Entity> List<E> getResultList(CriteriaQuery<E> criteriaQuery);

}
