package net.straininfo2.grs.idloader.bioproject.domain;

/**
 * Model of an external link supplied with a project.
 */
public class ExternalLink extends Link {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
