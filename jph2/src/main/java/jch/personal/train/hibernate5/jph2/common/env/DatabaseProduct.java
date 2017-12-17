package jch.personal.train.hibernate5.jph2.common.env;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public enum DatabaseProduct {

    ORACLE(
            new DataSourceConfiguration() {
                public void configure(PoolingDataSource ds, String connectionURL) {
                    ds.setClassName("oracle.jdbc.xa.client.OracleXADataSource");
                    ds.getDriverProperties().put(
                            "URL",
                            connectionURL != null ?
                                    connectionURL :
                                    "jdbc:oracle:thin:jph2/jph2@127.0.0.1:1521:DBG"
                    );
                }
            },
            org.hibernate.dialect.Oracle10gDialect.class.getName()
    );

    public DataSourceConfiguration configuration;

    public String hibernateDialect;

    private DatabaseProduct(DataSourceConfiguration configuration, String hibernateDialect) {
        this.configuration = configuration;
        this.hibernateDialect = hibernateDialect;
    }

    public interface DataSourceConfiguration {
        void configure(PoolingDataSource ds, String connectionURL);
    }




}
