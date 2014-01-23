package org.web4thejob.orm;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Repository
public class DataWriterServiceImpl implements DataWiterService {

    @Autowired
    ApplicationContext applicationContext;

    protected String getPersistenceUnitName(final Entity entity) {
        return getPersistenceUnitName(entity.getClass());
    }

    protected <E extends Entity> String getPersistenceUnitName(Class<E> entityClass) {
        for (Map.Entry<String, EntityManagerFactoryInfo> entry :
                BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
                        EntityManagerFactoryInfo.class).entrySet()) {

            if (entry.getValue().getPersistenceUnitInfo().getManagedClassNames().contains(entityClass.getName()
            )) {
                return entry.getValue().getPersistenceUnitInfo().getPersistenceUnitName();
            }
        }

        throw new IllegalArgumentException(entityClass.toString());
    }


    protected PlatformTransactionManager getJpaTransactionManager(final String unitName) {

        for (Map.Entry<String, JpaTransactionManager> entry :
                BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
                        JpaTransactionManager.class).entrySet()) {

            if (entry.getValue().getPersistenceUnitName().equals(unitName)) {
                return entry.getValue();
            }
        }

        throw new IllegalArgumentException(unitName);
    }

    protected EntityManager getEntityManager(final String unitName) {
        final EntityManagerFactory emf = EntityManagerFactoryUtils.findEntityManagerFactory(applicationContext,
                unitName);
        return EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
    }


    @Override
    public <E extends Entity> E persist(final E entity) {
        final String unitName = getPersistenceUnitName(entity);
        final PlatformTransactionManager platformTransactionManager = getJpaTransactionManager(unitName);
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        return transactionTemplate.execute(new TransactionCallback<E>() {
            @Override
            public E doInTransaction(TransactionStatus status) {
                final EntityManager entityManager = getEntityManager(unitName);
                E managed = entityManager.merge(entity);
                entityManager.persist(managed);
                return managed;
            }
        });
    }

    @Override
    public <E extends Entity> E find(final Class<E> entityClass, final Object primaryKey) {
        final String unitName = getPersistenceUnitName(entityClass);
        final PlatformTransactionManager platformTransactionManager = getJpaTransactionManager(unitName);
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        return transactionTemplate.execute(new TransactionCallback<E>() {
            @Override
            public E doInTransaction(TransactionStatus status) {
                final EntityManager entityManager = getEntityManager(unitName);
                return entityManager.find(entityClass, primaryKey);
            }
        });
    }

    @Override
    public void remove(final Entity entity) {
        final String unitName = getPersistenceUnitName(entity);
        final PlatformTransactionManager platformTransactionManager = getJpaTransactionManager(unitName);
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                final EntityManager entityManager = getEntityManager(unitName);
                Entity managed = entityManager.merge(entity);
                entityManager.remove(managed);
            }
        });
    }
}
