package net.straininfo2.grs.idloader.bioproject.domain;

public class DBXref extends Link {

    private String db;

    private String id;

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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
