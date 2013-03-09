package net.straininfo2.grs.idloader.bioproject.domain;

import javax.persistence.Entity;

/**
 * An administrative project. May contain extra information related to the
 * project type.
 */
@Entity
public class AdminBioProject extends BioProject {

    public enum ProjectSubType {
        eDisease,
        eComparativeGenomics,
        eMetagenome,
        eSingleOrganismDiscovery,
        eFundingInitiative,
        eAuthorizedAccess,
        eAccounting,
        eOther
    }

    private ProjectSubType subType;

    private String descriptionOther;

    public ProjectSubType getSubType() {
        return subType;
    }

    public void setSubType(ProjectSubType subType) {
        this.subType = subType;
    }

    public String getDescriptionOther() {
        return descriptionOther;
    }

    public void setDescriptionOther(String descriptionOther) {
        this.descriptionOther = descriptionOther;
    }
}
