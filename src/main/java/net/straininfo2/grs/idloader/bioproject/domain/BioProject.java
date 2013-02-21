package net.straininfo2.grs.idloader.bioproject.domain;

import java.util.Collection;

/**
 * Root of the domain model based on bioproject XML. In the serialization
 * format, packages are organised in package sets, and a package bundles
 * all related information about a project (project information itself,
 * submission data, etc.). Here, that hierarchy is flattened out as much
 * as possible, so submission data is linked to the project, as well as
 * whatever organism data can be linked to the project iself (each project,
 * no matter the type, has one organism). Project identifiers and description
 * are kept inline where possible.
 *
 * Some data is not mapped in these classes, mostly because it is not
 * included in public XML. This include contents of the tags:
 * ProjectAssembly, ProjectSubmission, ProjectLinks, ProjectPresentation,
 * SecondaryArchiveID, CenterID and top-level Submission tags.
 */
public class BioProject {

    private long projectId;

    private String accession;

    private Archive archive;

    private String name;

    private String title;

    private String description;

    private Collection<ProjectRelevance> projectRelevance;

    private Collection<String> locusTagPrefixes;

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public Archive getArchive() {
        return archive;
    }

    public void setArchive(Archive archive) {
        this.archive = archive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<ProjectRelevance> getProjectRelevance() {
        return projectRelevance;
    }

    public void setProjectRelevance(Collection<ProjectRelevance> projectRelevance) {
        this.projectRelevance = projectRelevance;
    }

    public Collection<String> getLocusTagPrefixes() {
        return locusTagPrefixes;
    }

    public void setLocusTagPrefixes(Collection<String> locusTagPrefixes) {
        this.locusTagPrefixes = locusTagPrefixes;
    }

}
