package net.straininfo2.grs.idloader.bioproject.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DBXref extends Link {

    private String db;

    private String dbId;

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    /**
     * List of identifiers as a string, separated by comma (',').
     *
     * @return
     */
    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

}
