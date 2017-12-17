package jch.personal.train.hibernate5.jph2.common.env;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.*;

public class JPATest extends TransactionManagerTest {

    public String persistenceUnitName;

    public String[] hbmResources;

    public JPASetup JPA;

    @BeforeClass
    public void beforeClass() {
        configurePersistenceUnit();
    }

    public void configurePersistenceUnit() {
        configurePersistenceUnit(null);
    }

    public void configurePersistenceUnit(String persistenceUnitName, String... hbmResources) {
        this.persistenceUnitName = persistenceUnitName;
        this.hbmResources = hbmResources;
    }

    @BeforeMethod
    public void beforeMethod() throws Exception {
        JPA = new JPASetup(TM.dbProd, persistenceUnitName, hbmResources);
    }

    public void afterJPABootstrap() {

    }

    public void afterMethod() {
        if (JPA != null) {
            beforeJPAClose();
            if (!"true".equals(System.getProperty("keepSchema"))) {
                JPA.dropSchema();
            }
            JPA.getEntityManagerFactory().close();
        }
    }

    public void beforeJPAClose() {

    }

    protected long copy(Reader input, Writer output) throws IOException {
        char[] buffer = new char[4096];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    protected String getTextResourceAsString(String resource) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
        if (is == null) {
            throw new IllegalArgumentException("Resource not found: " + resource);
        }
        StringWriter sw = new StringWriter();
        copy(new InputStreamReader(is), sw);
        return sw.toString();
    }

    protected Throwable unwrapRootCause(Throwable throwable) {
        return unwrapCauseOfType(throwable, null);
    }

    protected Throwable unwrapCauseOfType(Throwable throwable, Class<? extends Throwable> type) {
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            if (type != null && type.isAssignableFrom(current.getClass())) {
                return current;
            }
            throwable = current;
        }
        return throwable;
    }


}
