package jch.personal.train.hibernate5.jph2.ch03.simple;

import jch.personal.train.hibernate5.jph2.ch03.simple.model.Item;
import jch.personal.train.hibernate5.jph2.common.env.JPATest;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import javax.transaction.UserTransaction;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class AccessJPAMetamodel extends JPATest {

    @Override
    public void configurePersistenceUnit() {
        configurePersistenceUnit("SimpleXMLCompletePU");
    }

    @Test
    public void accessDynammicMetamodel() throws Exception {
        EntityManagerFactory emf = JPA.getEntityManagerFactory();

        Metamodel mm = emf.getMetamodel();

        Set<ManagedType<?>> managedTypes = mm.getManagedTypes();
        assertEquals(managedTypes.size(), 1);

        ManagedType itemType = managedTypes.iterator().next();
        assertEquals(itemType.getPersistenceType(), Type.PersistenceType.ENTITY);

        SingularAttribute nameAttribute =
                itemType.getSingularAttribute("name");
        assertEquals(nameAttribute.getJavaType(), String.class);

        assertFalse(nameAttribute.isOptional());

        SingularAttribute auctionEndAttribute =
                itemType.getSingularAttribute("auctionEnd");
        assertEquals(auctionEndAttribute.getJavaType(), Date.class);

        assertFalse(auctionEndAttribute.isCollection());
        assertFalse(auctionEndAttribute.isAssociation());

    }


    @Test
    public void queryStaticMetamodel() throws Exception {
        UserTransaction tx = TM.getUserTansaction();

        try {
            tx.begin();
            EntityManager em = JPA.createEntityManager();

            Item itemOne = new Item();
            itemOne.setName("This is some item");
            itemOne.setAuctionEnd(new Date(System.currentTimeMillis() + 100000));
            em.persist(itemOne);

            Item itemTwo = new Item();
            itemTwo.setName("Another item");
            itemTwo.setAuctionEnd(new Date(System.currentTimeMillis() + 100000));

            em.persist(itemTwo);

            tx.commit();
            em.close();

            em = JPA.createEntityManager();
            tx.begin();

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Item> query = cb.createQuery(Item.class);
            Root<Item> fromItem = query.from(Item.class);
            query.select(fromItem);

            List<Item> items = em.createQuery(query).getResultList();

            assertEquals(items.size(), 2);

            Path<String> namePath = fromItem.get("name");
            query.where(cb.like(namePath, cb.parameter(String.class, "pattern")));

            items = em.createQuery(query).setParameter("pattern", "%some item%").getResultList();

            assertEquals(items.size(), 1);
            assertEquals(items.iterator().next().getName(), "This is some item");

//            query.where(cb.like(fromItem.get(Item.name), cb.parameter(String.class, "pattern")));

            items = em.createQuery(query).setParameter("pattern", "%some item%").getResultList();

            assertEquals(items.size(), 1);
            assertEquals(items.iterator().next().getName(), "This is some item");


            tx.commit();
            em.close();
        } finally {
            tx.rollback();
        }
    }

}
