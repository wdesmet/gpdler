package net.straininfo2.grs.idloader.bioproject.domain;

/**
 * User: wdesmet
 * Date: 2/26/13
 * Time: 10:43 AM
 */
public class OrganismPhenotype {

    public enum BioticRelationship {
        eFreeLiving,
        eCommensal,
        eSymbiont,
        eEpisymbiont,
        eIntracellular,
        eParasite,
        eHost,
        eEndosymbiont
    }
    
    public enum TrophicLevel {
        eAutotroph,
        eHeterotroph,
        eMixotroph
    }
    
    private BioticRelationship bioticRelationship;
    
    private TrophicLevel trophicLevel;

    private String disease;

    public BioticRelationship getBioticRelationship() {
        return bioticRelationship;
    }

    public void setBioticRelationship(BioticRelationship bioticRelationship) {
        this.bioticRelationship = bioticRelationship;
    }

    public TrophicLevel getTrophicLevel() {
        return trophicLevel;
    }

    public void setTrophicLevel(TrophicLevel trophicLevel) {
        this.trophicLevel = trophicLevel;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

}
