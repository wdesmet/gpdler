package net.straininfo2.grs.idloader.bioproject.domain;


import javax.persistence.*;

@Entity
public class DBXref extends Link {

    private String db;

    private String dbId;

    private BioProject bioProject;

    @Column(length = 32)
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
    @Column(length = 128)
    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    @ManyToOne(optional = false)
    public BioProject getBioProject() {
        return bioProject;
    }

    public void setBioProject(BioProject bioProject) {
        this.bioProject = bioProject;
    }
}
