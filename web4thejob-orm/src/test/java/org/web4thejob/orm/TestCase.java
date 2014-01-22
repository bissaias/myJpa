package org.web4thejob.orm;

import com.joblet.one.Customer;
import com.joblet.two.Merchant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org/web4thejob/conf/beans.xml"})
public class TestCase {

    @Autowired
    private ApplicationContext applicationContext;

    @PersistenceContext(unitName = "Joblet1")
    private EntityManager emJ1;

    @PersistenceContext(unitName = "Joblet2")
    private EntityManager emJ2;

    @Test
    @Transactional("transactionManager1")
    public void test1() {


        Customer c = new Customer("first", "last");

        Assert.assertTrue(c.getId() == 0);

        emJ1.persist(c);

        Assert.assertTrue(c.getId() > 0);
    }

    @Test
    @Transactional("transactionManager2")
    public void test2() {


        Merchant m = new Merchant("first", "last");

        Assert.assertTrue(m.getId() == 0);

        emJ2.persist(m);

        Assert.assertTrue(m.getId() > 0);
    }


    @Test
    public void test3() {
        final PlatformTransactionManager tm1 = getJpaTransactionManager("Joblet1");
        Assert.assertNotNull(tm1);

        TransactionTemplate template1 = new TransactionTemplate(tm1); //  applicationContext.getBean(TransactionTemplate.class);
        template1.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                final EntityManager em = getEntityManager("Joblet1");
                Assert.assertNotNull(em);
                final Customer c = new Customer("first", "last");
                Assert.assertTrue(c.getId() == 0);

                em.persist(c);

                Assert.assertTrue(c.getId() > 0);
            }
        });

        final PlatformTransactionManager tm2 = getJpaTransactionManager("Joblet2");
        Assert.assertNotNull(tm2);

        TransactionTemplate template2 = new TransactionTemplate(tm2); //  applicationContext.getBean(TransactionTemplate.class);
        template2.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                final EntityManager em = getEntityManager("Joblet2");
                Assert.assertNotNull(em);
                Merchant m = new Merchant("first", "last");

                Assert.assertTrue(m.getId() == 0);

                em.persist(m);

                Assert.assertTrue(m.getId() > 0);
            }
        });

    }


    public PlatformTransactionManager getJpaTransactionManager(String unitName) {

        for (Map.Entry<String, JpaTransactionManager> entry :
                BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
                        JpaTransactionManager.class).entrySet()) {

            if (entry.getValue().getPersistenceUnitName().equals(unitName)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public EntityManager getEntityManager(String unitName) {

        EntityManagerFactory emf = EntityManagerFactoryUtils.findEntityManagerFactory(applicationContext, unitName);
        return EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
    }

}
