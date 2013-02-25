package net.straininfo2.grs.idloader.bioproject.domain;

/**
 * Superclass for external links. All links have a category and label in
 * common.
 */
public class Link {

    private String label;
    private String category;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
