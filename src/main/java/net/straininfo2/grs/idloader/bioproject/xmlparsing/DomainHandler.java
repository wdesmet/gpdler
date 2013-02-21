package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.domain.BioProject;

/**
 * An implementer of these classes will be passed domain objects by
 * DomainConverter.
 */
public interface DomainHandler {

    public void processBioProject(BioProject project);

}
