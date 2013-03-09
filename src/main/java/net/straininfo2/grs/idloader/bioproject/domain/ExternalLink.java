package net.straininfo2.grs.idloader.bioproject.domain;

import javax.persistence.Entity;

/**
 * Model of an external link supplied with a project.
 */
@Entity
public class ExternalLink extends Link {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
