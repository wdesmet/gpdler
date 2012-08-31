package net.straininfo2.grs.idloader.db;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class OracleProjectInfoLoader extends ProjectInfoLoader {
    @Override
    protected Date getLatestUpdate(String table) {
        return getTemplate().queryForObject(
                "SELECT MAX(UPDATED_AT) FROM " + table, new RowMapper<Date>() {
            @Override
            public Date mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                return rs.getDate(1);
            }
        });
    }

    @Override
    protected String insertAccessionMapping(String gtable, String accLabel) {
        return "INSERT INTO " + gtable
                + "(ID, PROJ_ID, " + accLabel +") "
                + "VALUES (" + gtable + "_SEQ.nextval, ?, ?)";
    }

    @Override
    protected String insertProjectLab(String table) {
        return "INSERT INTO " + table + "(id, proj_id, lab_name) "
                + "VALUES (" + getNamespace()
                + ".proj_labs_seq.nextval, ?, ?)";
    }

    @Override
    public void configureTables() {
        //TODO: also throw an error here
    }

    @Override
    protected void insertIntoGenomeProkComplete(long proj_id, Long chromosome_size, Long plasmid_number, Date date_released, Date date_modified) {
        getTemplate()
                .update("INSERT INTO "
                        + getNamespace()
                        + ".genome_prok_complete"
                        + "(proj_id, chromosome_number, plasmid_number, date_released, date_modified) "
                        + "VALUES (?, ?, ?, ?, ?)",
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
                        + " SET chromosome_number=?, plasmid_number=?, date_released=?, date_modified=? "
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
                        + "cds_nr=?, date_released=?, center_name=?, center_url=? WHERE proj_id=?",
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
                        + "cds_nr, date_released, center_name, center_url) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        proj_id,
                        sequence_availability,
                        refseq_accession,
                        genbank_accession,
                        contig_nr, cds_nr,
                        date_released, center_name,
                        center_url);
    }
}
