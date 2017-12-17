package jch.personal.train.hibernate5.jph2.ch02.helloworld;

import jch.personal.train.hibernate5.jph2.ch02.helloworld.model.Message;
import jch.personal.train.hibernate5.jph2.common.env.TransactionManagerTest;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class HelloWorldJPA extends TransactionManagerTest {

    @Test
    public void storeLoadMessage() {
        // 用于每一个配置好的持久化单元
        // 该类是线程安全的，且应用程序中访问该数据库的所有代码都应该共享它。
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("HelloWorldPU");


        try {

            {
                // 获得对标准事务的访问权
                UserTransaction tx = TM.getUserTansaction();
                tx.begin();

                // 通过创建一个EntityManage来开始一个与数据库的新会话，这是用于所有持久化的上下文。
                EntityManager em = emf.createEntityManager();

                // 创建一个Message新实例，并设置其为text属性。
                Message message = new Message();
                message.setText("Hello World!");

                // 持久化上下文登记该临时实例，让其持久化。Hibernate知道打算存储数据，但是这不必立即调用数据库。
                em.persist(message);

                // 提交该 事务，Hibernate会自动检查持久化上下文并执行必要的SQL INSERT语句。
                tx.commit();

                // 如果创建一个EntityManager，就必须关闭它。
                em.close();
            }

            {
                UserTransaction tx = TM.getUserTansaction();
                // 事务边界，与数据库的每一次交互都应该在明确的事务边界内发生，即使只是读取数据。
                tx.begin();


                EntityManager em = emf.createEntityManager();

                // 执行一条从数据库中检索所有Message实例的查询。JPQL语句
                List<Message> messages =
                        em.createQuery("select m from Message m").getResultList();

                assertEquals(messages.size(), 1);
                assertEquals(messages.get(0).getText(), "Hello World");

                // 可以变更一个属性值。
                messages.get(0).setText("Take me to your leader!");

                // 提交时，Hibernate会检查持久化上下文是否处于脏状态，且自动执行SQL UPDATE来将内存中的状态与数据库中的状态同步。
                tx.commit();

                em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TM.rollback();
            emf.close();
        }


    }

}
