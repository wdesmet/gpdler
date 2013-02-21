package net.straininfo2.grs.idloader.bioproject.domain;

/**
 * Possible fields in which a project can be relevant.
 */
public class ProjectRelevance {

    public enum RelevantField {
        AGRICULTURAL,
        MEDICAL,
        INDUSTRIAL,
        ENVIRONMENTAL,
        EVOLUTION,
        MODEL_ORGANISM,
        OTHER
    }

    private RelevantField relevantField;

    private String relevanceDescription;

    public ProjectRelevance() {

    }

    public ProjectRelevance(RelevantField field, String description) {
        this.relevantField = field;
        this.relevanceDescription = description;
    }

    public RelevantField getRelevantField() {
        return relevantField;
    }

    public void setRelevantField(RelevantField relevantField) {
        this.relevantField = relevantField;
    }

    public String getRelevanceDescription() {
        return relevanceDescription;
    }

    public void setRelevanceDescription(String relevanceDescription) {
        this.relevanceDescription = relevanceDescription;
    }
}
