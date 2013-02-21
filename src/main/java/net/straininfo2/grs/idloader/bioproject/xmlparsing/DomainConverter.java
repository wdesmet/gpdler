package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.bindings.Project;
import net.straininfo2.grs.idloader.bioproject.bindings.TypeArchiveID;
import net.straininfo2.grs.idloader.bioproject.bindings.TypePackage;
import net.straininfo2.grs.idloader.bioproject.domain.Archive;
import net.straininfo2.grs.idloader.bioproject.domain.BioProject;

public class DomainConverter implements PackageProcessor {

    private DomainHandler handler;

    public DomainConverter() {
        this.handler = new DomainHandler() {
            @Override
            public void processBioProject(BioProject project) {
                // default does nothing.
            }
        };
    }

    public DomainConverter(DomainHandler handler) {
        this.handler = handler;
    }

    public void addIdentifiers(BioProject project, Project.ProjectID id) {
        TypeArchiveID archiveID = id.getArchiveID();
        project.setProjectId(archiveID.getId().longValue());
        project.setAccession(archiveID.getAccession());
        project.setArchive(Archive.valueOf(archiveID.getArchive().value()));
    }

    private void addDescription(BioProject project, Project.ProjectDescr descr) {

    }

    private void addTypeSpecificInformation(BioProject project, Project.ProjectType type) {

    }

    @Override
    public void processPackage(TypePackage nextPackage) {
        Project xmlProject = nextPackage.getProject().getProject();
        //Submission xmlSubmission = nextPackage.getSubmission().getSubmission();
        BioProject project = new BioProject();
        addIdentifiers(project, xmlProject.getProjectID());
        addDescription(project, xmlProject.getProjectDescr());
        addTypeSpecificInformation(project, xmlProject.getProjectType());
        // finish by handing this project off to the next filter in the chain
        handler.processBioProject(project);
    }

}
