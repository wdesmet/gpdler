package net.straininfo2.grs.idloader.bioproject.domain;

import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Table(name="ProjectGrant")
public class Grant {

    private long id;

    private String title;

    private String agencyName;

    private String agencyAbbr;

    private String grantId;

    private BioProject bioProject;

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(length = 512)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        /*
        Annoyingly, titles are sometimes used for descriptions. We're truncating them here, in principle the
        schema could handle it differently (maybe a clob column instead of varchar, I guess)
        */
        if (title != null && title.length() > 512) {
            LoggerFactory.getLogger(this.getClass()).warn("Grant title exceeds length: {}, bioproject {}", title, bioProject);
            this.title = title.substring(0, 512);
        }
        else {
            this.title = title;
        }
    }

    @Column(length = 512)
    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    @Column(length = 64)
    public String getAgencyAbbr() {
        return agencyAbbr;
    }

    public void setAgencyAbbr(String agencyAbbr) {
        this.agencyAbbr = agencyAbbr.substring(0, 64); // should not exceed the field width
    }

    @Column(length = 64)
    public String getGrantId() {
        return grantId;
    }

    public void setGrantId(String grantId) {
        if (grantId.length() > 64) {
            grantId = grantId.substring(0, 64);
        }
        this.grantId = grantId;
    }

    @ManyToOne(optional =false)
    public BioProject getBioProject() {
        return bioProject;
    }

    public void setBioProject(BioProject bioProject) {
        this.bioProject = bioProject;
    }
}
