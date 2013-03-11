package net.straininfo2.grs.idloader.bioproject.domain;

import javax.persistence.*;

@Entity
public class UserTerm {

    private long id;

    private String value;

    private String term;

    private String category;

    private String units;

    private BioProject bioProject;

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Column(length = 32)
    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @ManyToOne(optional = false)
    public BioProject getBioProject() {
        return bioProject;
    }

    public void setBioProject(BioProject bioProject) {
        this.bioProject = bioProject;
    }
}
