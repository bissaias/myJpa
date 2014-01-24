package org.web4thejob.orm;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Repository
public class DataWriterServiceImpl implements DataWriterService {


    @Override
    public <E extends Entity> E persist(final E entity) {
        final String unitName = PersistenceUtils.getPersistenceUnitName(entity);
        final PlatformTransactionManager platformTransactionManager = PersistenceUtils.getJpaTransactionManager
                (unitName);
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        return transactionTemplate.execute(new TransactionCallback<E>() {
            @Override
            public E doInTransaction(TransactionStatus status) {
                final EntityManager entityManager = PersistenceUtils.getTransactionalEntityManager(unitName);
                E managed = entityManager.merge(entity);
                entityManager.persist(managed);
                return managed;
            }
        });
    }

    @Override
    public <E extends Entity> Collection<E> persist(final Collection<E> entities) {
        if (entities.size() == 0) return entities;

        final String unitName = PersistenceUtils.getPersistenceUnitName(entities.iterator().next());
        final PlatformTransactionManager platformTransactionManager = PersistenceUtils.getJpaTransactionManager
                (unitName);
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);

        final List<E> mList = new ArrayList<>(entities.size());
        mList.addAll(entities);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                final EntityManager entityManager = PersistenceUtils.getTransactionalEntityManager(unitName);
                for (int i = 0; i < mList.size(); i++) {
                    E managed = entityManager.merge(mList.get(i));
                    entityManager.persist(managed);
                    mList.set(i, managed);
                }
            }
        });


        return mList;
    }


    @Override
    public void remove(final Entity entity) {
        final String unitName = PersistenceUtils.getPersistenceUnitName(entity);
        final PlatformTransactionManager platformTransactionManager = PersistenceUtils.getJpaTransactionManager
                (unitName);
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                final EntityManager entityManager = PersistenceUtils.getTransactionalEntityManager(unitName);
                Entity managed = entityManager.merge(entity);
                entityManager.remove(managed);
            }
        });
    }

    @Override
    public void remove(final Collection<? extends Entity> entities) {
        if (entities.size() == 0) return;

        final String unitName = PersistenceUtils.getPersistenceUnitName(entities.iterator().next());
        final PlatformTransactionManager platformTransactionManager = PersistenceUtils.getJpaTransactionManager
                (unitName);
        final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                final EntityManager entityManager = PersistenceUtils.getTransactionalEntityManager(unitName);
                for (Entity entity : entities) {
                    Entity managed = entityManager.merge(entity);
                    entityManager.remove(managed);
                }
            }
        });
    }

}
