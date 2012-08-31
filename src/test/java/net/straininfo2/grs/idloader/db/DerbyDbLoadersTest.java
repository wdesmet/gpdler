package net.straininfo2.grs.idloader.db;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public class DerbyDbLoadersTest {

    @Before
    public void init() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
    }

    /**
     * A high-level test that tries to construct the tables in an in-memory db.
     */
    @Test
    public void testConfigureOfDatabase() throws SQLException {
        EmbeddedDataSource ds =  new EmbeddedDataSource();
        ds.setDatabaseName("memory:testdb");
        ds.setCreateDatabase("create");
        ProjectInfoLoader pl = new DerbyProjectInfoLoader();
        pl.setDataSource(ds);
        pl.setNamespace("test");
        pl.configureTables();
        MappingDbLoader ml = new DerbyMappingDbLoader();
        ml.setDataSource(ds);
        ml.setNamespace("test");
        ml.configureTables();
    }
}
