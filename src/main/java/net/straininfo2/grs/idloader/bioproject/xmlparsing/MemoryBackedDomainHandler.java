package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.domain.AdminBioProject;
import net.straininfo2.grs.idloader.bioproject.domain.BioProject;
import net.straininfo2.grs.idloader.bioproject.domain.SubmissionBioProject;
import net.straininfo2.grs.idloader.bioproject.xmlparsing.DomainHandler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Accepts BioProject elements, keeps track of them in memory.
 */
public class MemoryBackedDomainHandler implements DomainHandler {

    private final List<BioProject> projectList = new LinkedList<>();

    @Override
    public void processBioProject(BioProject project) {
        projectList.add(project);
    }

    @Override
    public void processAdminBioProject(AdminBioProject project) {
        processBioProject(project);
    }

    @Override
    public void processSubmissionBioProject(SubmissionBioProject project) {
        processBioProject(project);
    }

    @Override
    public void endParsing() {
    }

    public List<BioProject> getProjectList() {
        return Collections.unmodifiableList(projectList);
    }
}
