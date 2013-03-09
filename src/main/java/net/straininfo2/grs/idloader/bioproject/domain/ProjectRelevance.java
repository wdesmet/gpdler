package net.straininfo2.grs.idloader.bioproject.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Possible fields in which a project can be relevant.
 */
@Entity
public class ProjectRelevance {

    private long id;

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

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
