package jch.personal.train.hibernate5.jph2.common.env;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.xml.crypto.Data;
import java.util.logging.Logger;

public class TransactionManagerSetup {

    public static final String DATASOURCE_NAME = "jphTrainDB";

    private static final Logger logger = Logger.getLogger(TransactionManagerSetup.class.getName());

    protected final Context context = new InitialContext();

    protected final PoolingDataSource datasource;

    public final DatabaseProduct dbProd;

    public TransactionManagerSetup(DatabaseProduct dbProd) throws Exception {
        this(dbProd, null);
    }

    public TransactionManagerSetup(DatabaseProduct dbProd, String connectionURL) throws Exception {
        logger.fine("Starting database connection pool");
        logger.fine("Setting stable unique identifier for transaction recovery");
        TransactionManagerServices.getConfiguration().setServerId("127.0.0.1");

        logger.fine("Disabling JMX binding of manager in unit tests");
        TransactionManagerServices.getConfiguration().setDisableJmx(true);

        logger.fine("Disabling transaction logging for unit tests");
        TransactionManagerServices.getConfiguration().setJournal("null");

        logger.fine("Disabling warnings when the database isn't accessed in a transaction");
        TransactionManagerServices.getConfiguration().setWarnAboutZeroResourceTransaction(false);

        logger.fine("Creating connection pool");
        datasource = new PoolingDataSource();
        datasource.setUniqueName(DATASOURCE_NAME);
        datasource.setMinPoolSize(1);
        datasource.setMaxPoolSize(5);
        datasource.setPreparedStatementCacheSize(10);

        datasource.setIsolationLevel("READ_COMMITTED");
        datasource.setAllowLocalTransactions(true);


        logger.fine("Setting up database connection: " + dbProd);
        this.dbProd = dbProd;
        dbProd.configuration.configure(datasource, connectionURL);

        logger.fine("Initializing transaction and resource management");
        datasource.init();

    }

    public Context getNamingContext() {
        return context;
    }

    public UserTransaction getUserTansaction() {
        try {
            return (UserTransaction) getNamingContext().lookup("java:comp/UserTransaction");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public DataSource getDataSource() {
        try {
            return (DataSource) getNamingContext().lookup(DATASOURCE_NAME);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public void rollback() {
        UserTransaction tx = getUserTansaction();
        try {
            if (tx.getStatus() == Status.STATUS_ACTIVE ||
                    tx.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                tx.rollback();
            }
        } catch (Exception e) {
            System.err.println("Rollback of transaction failed, trace follows!");
            e.printStackTrace(System.err);
        }
    }

    public void stop() {
        logger.fine("Stopping database connection pool");
        datasource.close();
        TransactionManagerServices.getTransactionManager().shutdown();
    }
}
