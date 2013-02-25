package net.straininfo2.grs.idloader.db;

import org.springframework.jdbc.core.JdbcOperations;

public class DerbyMappingDbLoader extends MappingDbLoader {
    @Override
    protected String insertMappingQuery() {
        return "INSERT INTO " + getNamespace() + ".mappings (url, subject_type, link_name, category, provider_id, project_id, target_id)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    public void configureTables() {
        boolean schemaExists = getTemplate().queryForInt("SELECT COUNT(*) FROM SYS.SYSSCHEMAS WHERE SCHEMANAME=?", getNamespace().toUpperCase()) == 1;
        if (!schemaExists) {
            getTemplate().execute("CREATE SCHEMA " + getNamespace());
        }
        boolean tableExists = getTemplate().queryForInt("SELECT COUNT(*) FROM SYS.SYSTABLES WHERE TABLENAME='MAPPINGS'") == 1;
        if (!tableExists) {
            JdbcOperations ops = getTemplate();
            ops.execute("CREATE TABLE " + getNamespace() + ".PROVIDERS(" +
                    "ID   NUMERIC(8,0)," +
                    "NAME VARCHAR(128)," +
                    "ABBR VARCHAR(32)," +
                    "URL  VARCHAR(512)," +
                    "PRIMARY KEY (ID))");
            ops.execute("CREATE INDEX PROVIDER_ABBR_IDX ON " + getNamespace() + ".PROVIDERS(ABBR)");
            ops.execute("CREATE TABLE " + getNamespace() +".MAPPINGS(" +
                    "ID           BIGINT GENERATED ALWAYS AS IDENTITY," +
                    "URL          VARCHAR(1024)," +
                    "SUBJECT_TYPE VARCHAR(128)," +
                    "LINK_NAME    VARCHAR(128)," +
                    "CATEGORY     VARCHAR(128)," +
                    "PROVIDER_ID  NUMERIC(8,0)," +
                    "PROJECT_ID   BIGINT," +
                    "TARGET_ID    VARCHAR(20)," +
                    "PRIMARY KEY (ID)," +
                    "FOREIGN KEY (PROVIDER_ID) REFERENCES " + getNamespace() + ".PROVIDERS (ID)," +
                    "FOREIGN KEY (PROJECT_ID) REFERENCES " + getNamespace() + ".GENOME_PROJECTS (ID))");
            ops.execute("CREATE INDEX PROVIDER_TARGET_IDX ON " + getNamespace() + ".MAPPINGS(" +
                    "PROVIDER_ID," +
                    "TARGET_ID)");
        }
    }
}
