package jch.personal.train.hibernate5.jph2.ch02.helloworld;

import jch.personal.train.hibernate5.jph2.ch02.helloworld.model.Message;
import jch.personal.train.hibernate5.jph2.common.env.TransactionManagerTest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorBuilderImpl;
import org.hibernate.service.ServiceRegistry;
import org.testng.annotations.Test;

import javax.transaction.UserTransaction;
import java.util.List;

import static org.testng.Assert.assertEquals;


public class HelloWorldHIbernate extends TransactionManagerTest {

    protected void unusedSimpleBoot() {
        SessionFactory sessionFactory = new MetadataSources(
                new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build()
        ).buildMetadata().buildSessionFactory();
    }

    protected SessionFactory createSessionFactory() {

        /**
         * StarndardServiceRegistryBuilder用于创建带有链接方法调用的不可变的服务注册
         */
        StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();

        /**
         * 通过应用设置来配置服务注册
         */
        serviceRegistryBuilder
                .applySetting("hibernate.connection.datasource", "jphTrainDB")
                .applySetting("hibernate.formate_sql", "true")
                .applySetting("hibernate.use_sql_comments", "true")
                .applySetting("hibernate.hbm2ddl.auto", "create_drop");

        /**
         * 通过加载外部服务注册表配置文件的方式来配置服务注册
         */
        // serviceRegistryBuilder.loadProperties("hibernate-config.properties");

        // 启用JTA
        serviceRegistryBuilder.applySetting(
                Environment.TRANSACTION_COORDINATOR_STRATEGY,
                JtaTransactionCoordinatorBuilderImpl.class
        );
        ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();

        /**
         * 只能在服务注册中使用此配置
         */
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);

        /**
         * 构建了ServiceRegistry并让其不可变后，可以转到下一阶段了：告知Hibernate哪些持久化类是映射元数据的一部分。
         * 如下所示
         */
        /**
         * 将持久化类添加到此metadata sources
         */
        metadataSources.addAnnotatedClass(Message.class);

        // 添加hbm.xml文件
//        metadataSources.addFile(...)

        // 从jar文件中读取hbm.xml文件
//        metadataSources.addJar(...)

        MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();

        /**
         * 引导程序的下一阶段是构建Hibernate所需的所有元数据，以及从元数据源中获得的MetadataBuilder。
         * 然后可以查询该元数据以便与Hibernate的完整配置进行编程式交互，或者继续构建最终的SessionFactory。
         *
         */

        Metadata metadata = metadataBuilder.build();

        assertEquals(metadata.getEntityBindings().size(), 1);

        SessionFactory sessionFactory = metadata.buildSessionFactory();

        return sessionFactory;
    }


    @Test
    public void storeLoadMessage() throws Exception {
        SessionFactory sessionFactory = createSessionFactory();

        try {
            {
                // 获取访问标准的事务API UserTransaction，并且在此线程中开启一个事务。
                UserTransaction tx = TM.getUserTansaction();
                tx.begin();

                /**
                 * 在同一个线程中无论何时调用getCurrentSession()，都将获得同一个org.hibernate.Session。
                 * 它将会自动绑定到正在进行的事务，且当事务提交或回滚时自动关闭。
                 */
                Session session = sessionFactory.getCurrentSession();

                Message message = new Message();
                message.setText("Hello World!");

                /**
                 * 原生的Hibernate API与JPA非常相似，且大多数方法名都相同。
                 */
                session.persist(message);

                /**
                 * Hibernate会与数据库同步会话并在提交绑定的事务时自动关闭“当前”会话。
                 */
                tx.commit();
                // INSERT into MESSAGE (ID, TEXT) values (1, 'Hello World!')
            }

            {
                UserTransaction tx = TM.getUserTansaction();
                tx.begin();

                /**
                 * 一个标准的Hibernate查询是表示查询的一种类型安全的编程方式，会将其自动转译成SQL。
                 */
                List<Message> messages = sessionFactory.getCurrentSession().createCriteria(Message.class).list();
                // SELECT * FROM MESSAGE

                assertEquals(messages.size(), 1);
                assertEquals(messages.get(0).getText(), "Hello World!");

                tx.commit();
            }
        } finally {
            TM.rollback();
        }
    }

}
