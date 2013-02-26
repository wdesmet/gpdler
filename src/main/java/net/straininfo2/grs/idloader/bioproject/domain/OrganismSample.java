package net.straininfo2.grs.idloader.bioproject.domain;

/**
 * Sample information related to an organism. The names seem to suggest only
 * one of culture, cell or tissue sample info is present, but the schema is
 * configured to allow all three.
 */
public class OrganismSample {

    public enum CultureType {
        ePureCulture,
        eMixedCulture,
        eUncultered;
    }

    private CultureType cultureSampleInfo;
    
    private Boolean isIsolatedCell;
    
    private Boolean isTissueSample;

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
