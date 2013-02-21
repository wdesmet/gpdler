package net.straininfo2.grs.idloader.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class DerbyProjectInfoLoader extends ProjectInfoLoader {

    private final static Logger logger = LoggerFactory.getLogger(ProjectInfoLoader.class);
    @Override
    protected Date getLatestUpdate(String table) {
        return null; // not implemented here
    }

    @Override
    protected String insertAccessionMapping(String gtable, String accLabel) {
        return "INSERT INTO " + gtable
                + "(PROJ_ID, " + accLabel +") "
                + "VALUES (?, ?)";
    }

    @Override
    protected String insertProjectLab(String table) {
        return "INSERT INTO " + table + "(proj_id, lab_name) VALUES (?, ?)";
    }

    @Override
    public void configureTables() {
        boolean schemaExists = getTemplate().queryForInt("SELECT COUNT(*) FROM SYS.SYSSCHEMAS WHERE SCHEMANAME=?", getNamespace().toUpperCase()) == 1;
        if (!schemaExists) {
            getTemplate().execute("CREATE SCHEMA " + getNamespace());
            getTemplate().execute("CREATE TABLE " + getNamespace() + ".GENOME_PROJECTS(ID BIGINT PRIMARY KEY)");
            getTemplate().execute("CREATE TABLE " + getNamespace() + ".PROJECTS_LABS(" +
                    "ID       BIGINT GENERATED ALWAYS AS IDENTITY," +
                    "PROJ_ID  BIGINT," +
                    "LAB_NAME VARCHAR(255)," +
                    "PRIMARY KEY (ID)," +
                    "UNIQUE (PROJ_ID, LAB_NAME))");
            getTemplate().execute("CREATE TABLE " + getNamespace() + ".GENOME_EUK(" +
                    "PROJ_ID           BIGINT," +
                    "ORGANISM_NAME     VARCHAR(255)," +
                    "ORGANISM_GROUP    VARCHAR(50)," +
                    "ORGANISM_SUBGROUP VARCHAR(50)," +
                    "NCBI_TAXON_ID     NUMERIC(10,0)," +
                    "GENOME_SIZE       NUMERIC(14,4)," +
                    "NR_CHROMOSOMES    NUMERIC(8,0)," +
                    "SEQUENCE_STATUS   VARCHAR(20)," +
                    "SEQUENCE_METHOD   VARCHAR(20)," +
                    "COVERAGE          VARCHAR(50)," +
                    "RELEASE_DATE DATE," +
                    "PRIMARY KEY (PROJ_ID))");
            getTemplate().execute("CREATE TABLE " + getNamespace() + ".GENOME_ENV(" +
                    "PROJ_ID           BIGINT," +
                    "PARENT_PROJ_ID    NUMERIC(10,0)," +
                    "TITLE             VARCHAR(255)," +
                    "METAGENOME_SOURCE VARCHAR(50)," +
                    "METAGENOME_TYPE   NUMERIC(4,0)," +
                    "RELEASE_DATE DATE," +
                    "PRIMARY KEY (PROJ_ID))");
            getTemplate().execute("CREATE TABLE " + getNamespace() + ".GENOME_PROK(" +
                    "PROJ_ID         BIGINT," +
                    "REFSEQ          NUMERIC(10,0)," +
                    "NCBI_TAXON_ID   NUMERIC(10,0)," +
                    "ORGANISM_NAME   VARCHAR(100)," +
                    "SUPER_KINGDOM   VARCHAR(50)," +
                    "TAXON_GROUP     VARCHAR(50)," +
                    "SEQUENCE_STATUS VARCHAR(50)," +
                    "GENOME_SIZE     NUMERIC(14,4)," +
                    "GC_CONTENT      VARCHAR(20)," +
                    "GRAM_STAIN      NUMERIC(1,0)," +
                    "SHAPE           VARCHAR(50)," +
                    "ARRANGEMENT     VARCHAR(50)," +
                    "ENDOSPORES      VARCHAR(50)," +
                    "MOTILITY        VARCHAR(50)," +
                    "SALINITY        VARCHAR(50)," +
                    "OXYGEN_REQ      VARCHAR(50)," +
                    "HABITAT         VARCHAR(50)," +
                    "TEMP_RANGE      VARCHAR(50)," +
                    "OPTIMAL_TEMP    VARCHAR(50)," +
                    "PATHOGENIC_IN   VARCHAR(50)," +
                    "DISEASE         VARCHAR(512)," +
                    "PRIMARY KEY (PROJ_ID))");
            getTemplate().execute("CREATE TABLE " + getNamespace() + ".GENOME_PROK_COMPLETE(" +
                    "PROJ_ID           BIGINT," +
                    "CHROMOSOME_NUMBER NUMERIC(5,0)," +
                    "PLASMID_NUMBER    NUMERIC(5,0)," +
                    "DATE_RELEASED DATE," +
                    "DATE_MODIFIED DATE," +
                    "CREATED_AT DATE," +
                    "UPDATED_AT DATE," +
                    "PRIMARY KEY (PROJ_ID))");
            getTemplate().execute("CREATE TABLE " + getNamespace() + ".GENOME_PROK_INPROGRESS" +
                    "(" +
                    "PROJ_ID           BIGINT," +
                    "REFSEQ_ACCESSION  VARCHAR(20)," +
                    "GENBANK_ACCESSION VARCHAR(20)," +
                    "CONTIG_NR         NUMERIC(10,0)," +
                    "CDS_NR            NUMERIC(10,0)," +
                    "DATE_RELEASED     DATE," +
                    "CENTER_NAME           VARCHAR(255)," +
                    "CENTER_URL            VARCHAR(1023)," +
                    "SEQUENCE_AVAILABILITY VARCHAR(20)," +
                    "CREATED_AT DATE," +
                    "UPDATED_AT DATE," +
                    "PRIMARY KEY (PROJ_ID))");
            getTemplate().execute("CREATE TABLE " + getNamespace() + ".GENOME_PROJ_ACC(" +
                    "ID               BIGINT GENERATED ALWAYS AS IDENTITY," +
                    "PROJ_ID          BIGINT," +
                    "ACCESSION_NUMBER VARCHAR(255)," +
                    "PRIMARY KEY (ID)," +
                    "UNIQUE (PROJ_ID, ACCESSION_NUMBER)," +
                    "FOREIGN KEY (PROJ_ID) REFERENCES " + getNamespace() + ".GENOME_PROJECTS(ID))");
            getTemplate().execute("CREATE UNIQUE INDEX " + getNamespace() + ".PROJ_ACC_IDX ON "
                    + getNamespace() + ".GENOME_PROJ_ACC(PROJ_ID, ACCESSION_NUMBER)");
            getTemplate().execute("CREATE TABLE " + getNamespace() + ".GENOME_PROJ_REFSEQ(" +
                    "ID         BIGINT GENERATED ALWAYS AS IDENTITY," +
                    "PROJ_ID    BIGINT," +
                    "REFSEQ_ACC VARCHAR(255)," +
                    "PRIMARY KEY (ID)," +
                    "UNIQUE (PROJ_ID, REFSEQ_ACC)," +
                    "FOREIGN KEY (PROJ_ID) REFERENCES " + getNamespace() + ".GENOME_PROJECTS(ID))");
            getTemplate().execute("CREATE UNIQUE INDEX " + getNamespace() + ".PROJ_REFSEQ_IDX ON " +
                    getNamespace() + ".GENOME_PROJ_REFSEQ(PROJ_ID, REFSEQ_ACC)");
        }
        else {
            logger.debug("Schema already exists, continuing.");
        }
    }

    @Override
    protected void insertIntoGenomeProkComplete(long proj_id, Long chromosome_size, Long plasmid_number, Date date_released, Date date_modified) {
        getTemplate()
                .update("INSERT INTO "
                        + getNamespace()
                        + ".genome_prok_complete"
                        + "(proj_id, chromosome_number, plasmid_number, date_released, date_modified, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, CURRENT_DATE, CURRENT_DATE)",
                        proj_id, chromosome_size,
                        plasmid_number,
                        date_released,
                        date_modified);
    }

    @Override
    protected void updateGenomeProkComplete(long proj_id, Long chromosome_size, Long plasmid_number, Date date_released, Date date_modified) {
        getTemplate()
                .update("UPDATE "
                        + getNamespace()
                        + ".genome_prok_complete"
                        + " SET chromosome_number=?, plasmid_number=?, date_released=?, date_modified=? , updated_at=CURRENT_DATE "
                        + "WHERE proj_id=?",
                        chromosome_size,
                        plasmid_number,
                        date_released,
                        date_modified, proj_id);
    }

    @Override
    protected void updateProkaryotesInProgress(long proj_id, String sequence_availability, String refseq_accession, String genbank_accession, Long contig_nr, Long cds_nr, Date date_released, String center_name, String center_url) {
        getTemplate()
                .update("UPDATE "
                        + getNamespace()
                        + ".genome_prok_inprogress"
                        + " SET sequence_availability=?, refseq_accession=?, genbank_accession=?, contig_nr=?,"
                        + "cds_nr=?, date_released=?, center_name=?, center_url=?, updated_at=CURRENT_DATE WHERE proj_id=?",
                        sequence_availability,
                        refseq_accession,
                        genbank_accession,
                        contig_nr, cds_nr,
                        date_released, center_name,
                        center_url, proj_id);
    }

    @Override
    protected void insertIntoProkaryotesInProgress(long proj_id, String sequence_availability, String refseq_accession, String genbank_accession, Long contig_nr, Long cds_nr, Date date_released, String center_name, String center_url) {
        getTemplate()
                .update("INSERT INTO "
                        + getNamespace()
                        + ".genome_prok_inprogress"
                        + "(proj_id, sequence_availability, refseq_accession, genbank_accession, contig_nr,"
                        + "cds_nr, date_released, center_name, center_url, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE, CURRENT_DATE)",
                        proj_id,
                        sequence_availability,
                        refseq_accession,
                        genbank_accession,
                        contig_nr, cds_nr,
                        date_released, center_name,
                        center_url);
    }

    @Override
    protected void removeOldEntries(String table) {
        int numrows = getTemplate().update(
                "DELETE FROM " + table + " WHERE UPDATED_AT < "
                        + "(SELECT CAST({fn TIMESTAMPADD(SQL_TSI_DAY,-1,MAX(UPDATED_AT))} AS DATE) FROM " + table + ")");
        logger.debug("{} old rows deleted from {}", numrows, table);
    }
}
