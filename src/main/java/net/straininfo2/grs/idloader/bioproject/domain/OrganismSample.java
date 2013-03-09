package net.straininfo2.grs.idloader.bioproject.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Sample information related to an organism. The names seem to suggest only
 * one of culture, cell or tissue sample info is present, but the schema is
 * configured to allow all three.
 */
@Entity
public class OrganismSample {

    private long id;

    public enum CultureType {
        ePureCulture,
        eMixedCulture,
        eUncultered;
    }

    private CultureType cultureSampleInfo;
    
    private Boolean isIsolatedCell;
    
    private Boolean isTissueSample;

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CultureType getCultureSampleInfo() {
        return cultureSampleInfo;
    }

    public void setCultureSampleInfo(CultureType cultureSampleInfo) {
        this.cultureSampleInfo = cultureSampleInfo;
    }

    public Boolean getIsolatedCell() {
        return isIsolatedCell;
    }

    public void setIsolatedCell(Boolean isolatedCell) {
        isIsolatedCell = isolatedCell;
    }

    public Boolean getTissueSample() {
        return isTissueSample;
    }

    public void setTissueSample(Boolean tissueSample) {
        isTissueSample = tissueSample;
    }

}
