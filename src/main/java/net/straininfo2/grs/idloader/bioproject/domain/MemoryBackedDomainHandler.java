package net.straininfo2.grs.idloader.bioproject.domain;

import net.straininfo2.grs.idloader.bioproject.xmlparsing.DomainHandler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Accepts BioProject elements, keeps track of them in memory.
 */
public class MemoryBackedDomainHandler implements DomainHandler {

    private List<BioProject> projectList = new LinkedList<>();

    @Override
    public void processBioProject(BioProject project) {
        projectList.add(project);
    }

    public List<BioProject> getProjectList() {
        return Collections.unmodifiableList(projectList);
    }
}
