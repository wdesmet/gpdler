package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.domain.AdminBioProject;
import net.straininfo2.grs.idloader.bioproject.domain.BioProject;
import net.straininfo2.grs.idloader.bioproject.domain.SubmissionBioProject;

/**
 * An implementer of these classes will be passed domain objects by
 * DomainConverter.
 */
public interface DomainHandler {

    public void processBioProject(BioProject project);

    public void processAdminBioProject(AdminBioProject project);

    public void processSubmissionBioProject(SubmissionBioProject project);

    /**
     * Called when last project has been parsed and passed on.
     */
    public void endParsing();

}
