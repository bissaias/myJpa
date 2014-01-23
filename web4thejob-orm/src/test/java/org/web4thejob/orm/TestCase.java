package org.web4thejob.orm;

import com.joblet.one.Customer;
import com.joblet.two.Merchant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
        DataWiterService dataWiterService = applicationContext.getBean(DataWiterService.class);


        for (int i = 1; i <= 100; i++) {
            Customer c = new Customer("first", "last");
            Assert.assertTrue(c.getId() == 0);
            c = dataWiterService.persist(c);
            Assert.assertTrue(c.getId() > 0);

            Merchant m = new Merchant("first", "last");
            Assert.assertTrue(m.getId() == 0);
            m = dataWiterService.persist(m);
            Assert.assertTrue(m.getId() > 0);
        }

        for (int i = 1; i <= 100; i++) {
            Merchant m = new Merchant("first", "last");
            Assert.assertTrue(m.getId() == 0);
            m = dataWiterService.persist(m);
            Assert.assertTrue(m.getId() > 0);

            Customer c = new Customer("first", "last");
            Assert.assertTrue(c.getId() == 0);
            c = dataWiterService.persist(c);
            Assert.assertTrue(c.getId() > 0);
        }

    }

    @Test
    public void test4() {
        DataWiterService dataWiterService = applicationContext.getBean(DataWiterService.class);


        for (int i = 1; i <= 10; i++) {
            Customer c = new Customer("first", "last");
            Assert.assertTrue(c.getId() == 0);
            c = dataWiterService.persist(c);
            Assert.assertTrue(c.getId() > 0);

            Merchant m = new Merchant("first", "last");
            Assert.assertTrue(m.getId() == 0);
            m = dataWiterService.persist(m);
            m.setFirstName("123");
            m = dataWiterService.persist(m);
            Assert.assertTrue(m.getId() > 0);
        }


        final Customer c = dataWiterService.find(Customer.class, 1L);
        dataWiterService.remove(c);
    }

}
