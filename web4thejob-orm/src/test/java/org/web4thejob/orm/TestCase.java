package org.web4thejob.orm;

import com.joblet.one.Customer;
import com.joblet.two.Merchant;
import com.joblet.two.Merchant_;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

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
        DataWriterService dataWriterService = applicationContext.getBean(DataWriterService.class);


        for (int i = 1; i <= 100; i++) {
            Customer c = new Customer("first", "last");
            Assert.assertTrue(c.getId() == 0);
            c = dataWriterService.persist(c);
            Assert.assertTrue(c.getId() > 0);

            Merchant m = new Merchant("first", "last");
            Assert.assertTrue(m.getId() == 0);
            m = dataWriterService.persist(m);
            Assert.assertTrue(m.getId() > 0);
        }

        for (int i = 1; i <= 100; i++) {
            Merchant m = new Merchant("first", "last");
            Assert.assertTrue(m.getId() == 0);
            m = dataWriterService.persist(m);
            Assert.assertTrue(m.getId() > 0);

            Customer c = new Customer("first", "last");
            Assert.assertTrue(c.getId() == 0);
            c = dataWriterService.persist(c);
            Assert.assertTrue(c.getId() > 0);
        }

    }

    @Test
    public void test4() {
        DataWriterService dataWriterService = applicationContext.getBean(DataWriterService.class);

        Customer c = new Customer("first", "last");
        Assert.assertTrue(c.getId() == 0);
        c = dataWriterService.persist(c);
        Assert.assertTrue(c.getId() > 0);


        Merchant m = new Merchant("first", "last");
        Assert.assertTrue(m.getId() == 0);
        m = dataWriterService.persist(m);
        m.setFirstName("123");
        m = dataWriterService.persist(m);
        Assert.assertTrue(m.getId() > 0);

        DataReaderService dataReaderService = applicationContext.getBean(DataReaderService.class);
        c = dataReaderService.find(Customer.class, c.getId());
        dataWriterService.remove(c);
    }

    @Test(expected = IllegalStateException.class)
    public void test5() {
        DataWriterService dataWriterService = applicationContext.getBean(DataWriterService.class);

        Customer c = new Customer("first", "last");
        PersistenceUtils.getTransactionalEntityManager(c);
    }

    @Test
    public void test6() {
        test3();

        DataReaderService dataReaderService = applicationContext.getBean(DataReaderService.class);

        CriteriaQuery<Customer> cq = dataReaderService.getCriteriaQuery(Customer.class);
        Root<Customer> root = cq.from(Customer.class);

        List<Customer> customers = dataReaderService.getResultList(cq);

        assertTrue(customers.size() > 0);

    }

    @Test
    public void test7() {
        DataWriterService dataWriterService = applicationContext.getBean(DataWriterService.class);

        Customer c1 = new Customer("first", "last");
        Customer c2 = new Customer("first", "last");

        for (Customer c : dataWriterService.persist(Arrays.asList(c1, c2))) {
            assertTrue(c.getId() > 0);
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void test8() {
        DataWriterService dataWriterService = applicationContext.getBean(DataWriterService.class);

        Customer c = new Customer("first", null);
        dataWriterService.persist(c);
    }

    @Test
    public void test9() {
        DataWriterService dataWriterService = applicationContext.getBean(DataWriterService.class);
        Merchant m = new Merchant("first", "last");
        m = dataWriterService.persist(m);

        DataReaderService dataReaderService = applicationContext.getBean(DataReaderService.class);
        dataReaderService.find(Merchant.class, m.getId());

        assertTrue(PersistenceUtils.getEntityManagerFactory(m).getCache().contains(Merchant.class, m.getId()));

    }

    @Test
    public void test10() {
        DataWriterService dataWriterService = applicationContext.getBean(DataWriterService.class);
        Merchant m = new Merchant("first", "last");
        dataWriterService.persist(m);
        m = new Merchant("first", "last");
        m = dataWriterService.persist(m);

        DataReaderService dataReaderService = applicationContext.getBean(DataReaderService.class);
        CriteriaBuilder cb = dataReaderService.getCriteriaBuilder(Merchant.class);
        CriteriaQuery<Merchant> cq = dataReaderService.getCriteriaQuery(Merchant.class);
        Root<Merchant> root = cq.from(Merchant.class);
        Predicate condition1 = cb.equal(root.get(Merchant_.id), m.getId());
        Predicate condition2 = cb.equal(root.get(Merchant_.firstName), m.getFirstName());
        cq.where(cb.and(condition1, condition2));

        assertNotNull(dataReaderService.getSingleResult(cq));

    }

    @Test
    public void test11() {
        DataWriterService dataWriterService = applicationContext.getBean(DataWriterService.class);
        Merchant m = new Merchant("first", "last");
        dataWriterService.persist(m);
        m = new Merchant("first", "last");
        m = dataWriterService.persist(m);

        DataReaderService dataReaderService = applicationContext.getBean(DataReaderService.class);
        CriteriaBuilder cb = dataReaderService.getCriteriaBuilder(Merchant.class);
        CriteriaQuery<Merchant> cq = dataReaderService.getCriteriaQuery(Merchant.class);
        Root<Merchant> root = cq.from(Merchant.class);
        Predicate condition1 = cb.equal(root.get(Merchant_.id), m.getId());
        Predicate condition2 = cb.equal(root.get(Merchant_.firstName), "xxxxx-xxxx-xxxx");
        cq.where(cb.and(condition1, condition2));

        assertNull(dataReaderService.getSingleOrNoResult(cq));
    }

}
