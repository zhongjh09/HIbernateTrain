package jch.personal.train.hibernate5.jph2.common.env;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.util.Locale;

/**
 * 在测试开始前或结束后启动或停止事务管理器/数据库连接池。
 * 所有的测试均使用同一个TransactionManagerSetup。
 * 在具体的需要通过JTA事务管理和数据库连接的测试类中调用静态方法TransactionManagerTest.TM
 * 测试参数database和一个connectionURL是非必须的。
 * 默认使用H2内存数据库，在每一个test运行时自动创建、销毁。
 */
public class TransactionManagerTest {

    static public TransactionManagerSetup TM;

    @Parameters({"database", "connectionURL"})
    @BeforeSuite()
    public void beforeSuite(@Optional String database,
                            @Optional String connectionURL) throws Exception {
        TM = new TransactionManagerSetup(
                database != null
                        ? DatabaseProduct.valueOf(database.toUpperCase(Locale.PRC))
                        : DatabaseProduct.ORACLE,
                connectionURL
        );
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        if (TM != null) {
            TM.stop();
        }
    }

}
