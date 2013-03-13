package net.straininfo2.grs.idloader.bioproject.domain;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Set;

@Entity
public class OrganismMorphology {

    private long id;

    private Gram gram;

    private Boolean enveloped;

    private Set<Shape> shapes;
    
    private Boolean endospores;
    
    private Boolean motility;

    public enum Gram {
        eNegative,
        ePositive
    }

    public enum Shape {
        eBacilli,
        eCocci,
        eSpirilla,
        eCoccobacilli,
        eFilamentous,
        eVibrios,
        eFusobacteria,
        eSquareShaped,
        eCurvedShaped,
        eTailed
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Gram getGram() {
        return gram;
    }

    public void setGram(Gram gram) {
        this.gram = gram;
    }

    public Boolean getEnveloped() {
        return enveloped;
    }

    public void setEnveloped(Boolean enveloped) {
        this.enveloped = enveloped;
    }

    @ElementCollection
    public Set<Shape> getShapes() {
        return shapes;
    }

    public void setShapes(Set<Shape> shapes) {
        this.shapes = shapes;
    }

    public Boolean getEndospores() {
        return endospores;
    }

    public void setEndospores(Boolean endospores) {
        this.endospores = endospores;
    }

    public Boolean getMotility() {
        return motility;
    }

    public void setMotility(Boolean motility) {
        this.motility = motility;
    }

}
