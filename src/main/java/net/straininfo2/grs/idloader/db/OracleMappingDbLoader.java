package net.straininfo2.grs.idloader.db;

public class OracleMappingDbLoader extends MappingDbLoader {
    @Override
    protected String insertMappingQuery() {
        return "INSERT INTO " + getNamespace() + ".mappings (id, url, subject_type, link_name, category, provider_id, project_id, target_id)" +
                "VALUES (" + getNamespace() + ".mapping_seq.nextval, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    public void configureTables() {
        // Use the supplied SQL script instead!
        // TODO: this should throw an error if the tables don't exist, instead of crashing later
    }
}
