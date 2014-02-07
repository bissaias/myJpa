package org.web4thejob.orm;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.web4thejob.context.ContextUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Repository
public class DataReaderServiceImpl implements DataReaderService {

    @Override
    public <E extends Entity> E find(final Class<E> entityClass, final Object primaryKey) {
        final String unitName = PersistenceUtils.getPersistenceUnitName(entityClass);
        final PlatformTransactionManager platformTransactionManager = PersistenceUtils.getJpaTransactionManager
                (unitName);
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        return transactionTemplate.execute(new TransactionCallback<E>() {
            @Override
            public E doInTransaction(TransactionStatus status) {
                final EntityManager entityManager = PersistenceUtils.getTransactionalEntityManager(unitName);
                return entityManager.find(entityClass, primaryKey);
            }
        });
    }

    @Override
    public <E extends Entity> CriteriaQuery<E> getCriteriaQuery(Class<E> entityClass) {
        final EntityManagerFactory emf = EntityManagerFactoryUtils.findEntityManagerFactory(ContextUtils
                .getRootContext(),
                PersistenceUtils.getPersistenceUnitName(entityClass));

        return emf.getCriteriaBuilder().createQuery(entityClass);
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder(Class<? extends Entity> entityClass) {
        final EntityManagerFactory emf = EntityManagerFactoryUtils.findEntityManagerFactory(ContextUtils
                .getRootContext(),
                PersistenceUtils.getPersistenceUnitName(entityClass));

        return emf.getCriteriaBuilder();
    }

    @Override
    public <E extends Entity> List<E> getResultList(final CriteriaQuery<E> criteriaQuery) {
        final String unitName = PersistenceUtils.getPersistenceUnitName(criteriaQuery.getResultType());
        final PlatformTransactionManager platformTransactionManager = PersistenceUtils.getJpaTransactionManager
                (unitName);
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        return transactionTemplate.execute(new TransactionCallback<List<E>>() {
            @Override
            public List<E> doInTransaction(TransactionStatus status) {
                final EntityManager entityManager = PersistenceUtils.getTransactionalEntityManager(unitName);
                return entityManager.createQuery(criteriaQuery).getResultList();
            }
        });
    }

    @Override
    public <E extends Entity> E getSingleResult(final CriteriaQuery<E> criteriaQuery) {
        final String unitName = PersistenceUtils.getPersistenceUnitName(criteriaQuery.getResultType());
        final PlatformTransactionManager platformTransactionManager = PersistenceUtils.getJpaTransactionManager
                (unitName);
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        return transactionTemplate.execute(new TransactionCallback<E>() {
            @Override
            public E doInTransaction(TransactionStatus status) {
                final EntityManager entityManager = PersistenceUtils.getTransactionalEntityManager(unitName);
                return entityManager.createQuery(criteriaQuery).getSingleResult();
            }
        });
    }

    @Override
    public <E extends Entity> E getSingleOrNoResult(final CriteriaQuery<E> criteriaQuery) {
        try {
            return getSingleResult(criteriaQuery);
        } catch (NoResultException e) {
            return null;
        }
    }

}
