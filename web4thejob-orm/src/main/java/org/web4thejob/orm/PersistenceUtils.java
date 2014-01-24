package org.web4thejob.orm;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.web4thejob.context.ContextUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public abstract class PersistenceUtils {

    public static String getPersistenceUnitName(final Entity entity) {
        return getPersistenceUnitName(entity.getClass());
    }

    public static String getPersistenceUnitName(Class<? extends Entity> entityClass) {
        for (Map.Entry<String, EntityManagerFactoryInfo> entry :
                BeanFactoryUtils.beansOfTypeIncludingAncestors(ContextUtils.getRootContext(),
                        EntityManagerFactoryInfo.class).entrySet()) {

            if (entry.getValue().getPersistenceUnitInfo().getManagedClassNames().contains(entityClass.getName()
            )) {
                return entry.getValue().getPersistenceUnitInfo().getPersistenceUnitName();
            }
        }

        throw new IllegalArgumentException(entityClass.toString());
    }

    public static EntityManagerFactory getEntityManagerFactory(Entity entity) {
        return getEntityManagerFactory(entity.getClass());
    }

    public static EntityManagerFactory getEntityManagerFactory(Class<? extends Entity> entityClass) {
        for (Map.Entry<String, EntityManagerFactoryInfo> entry :
                BeanFactoryUtils.beansOfTypeIncludingAncestors(ContextUtils.getRootContext(),
                        EntityManagerFactoryInfo.class).entrySet()) {

            if (entry.getValue().getPersistenceUnitInfo().getManagedClassNames().contains(entityClass.getName()
            )) {
                return entry.getValue().getNativeEntityManagerFactory();
            }
        }

        throw new IllegalArgumentException(entityClass.toString());
    }

    public static PlatformTransactionManager getJpaTransactionManager(final Entity entity) {
        return getJpaTransactionManager(getPersistenceUnitName(entity));
    }

    public static PlatformTransactionManager getJpaTransactionManager(final String unitName) {

        for (Map.Entry<String, JpaTransactionManager> entry :
                BeanFactoryUtils.beansOfTypeIncludingAncestors(ContextUtils.getRootContext(),
                        JpaTransactionManager.class).entrySet()) {

            if (entry.getValue().getPersistenceUnitName().equals(unitName)) {
                return entry.getValue();
            }
        }

        throw new IllegalArgumentException(unitName);
    }

    public static EntityManager getTransactionalEntityManager(final Entity entity) {
        return getTransactionalEntityManager(getPersistenceUnitName(entity));
    }

    public static EntityManager getTransactionalEntityManager(final String unitName) {
        final EntityManagerFactory emf = EntityManagerFactoryUtils.findEntityManagerFactory(ContextUtils
                .getRootContext(),
                unitName);
        final EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);

        if (em == null) {
            throw new IllegalStateException("No active transaction found.");
        }

        return em;
    }

}
