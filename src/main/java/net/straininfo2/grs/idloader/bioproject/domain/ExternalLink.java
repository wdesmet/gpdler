package net.straininfo2.grs.idloader.bioproject.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Model of an external link supplied with a project.
 */
@Entity
public class ExternalLink extends Link {

    private String url;

    private BioProject bioProject;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @ManyToOne(optional = false)
    public BioProject getBioProject() {
        return bioProject;
    }

    public void setBioProject(BioProject bioProject) {
        this.bioProject = bioProject;
    }
}
