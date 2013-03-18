package net.straininfo2.grs.idloader.bioproject.domain;

import net.straininfo2.grs.idloader.bioproject.domain.mappings.Mapping;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
@Entity
public class BioProject {

    private long projectId;

    private String accession;

    private Archive archive;

    private String name;

    private String title;

    private String description;

    private Set<ProjectRelevance> projectRelevance = new HashSet<>();

    private Set<String> locusTagPrefixes = new HashSet<>();

    private Set<Publication> publications = new HashSet<>();

    private Set<ExternalLink> externalLinks = new HashSet<>();

    private Set<DBXref> crossReferences = new HashSet<>();

    private Set<UserTerm> userTerms = new HashSet<>();

    private Set<Grant> grants = new HashSet<>();

    private Set<Mapping> mappings;

    // always one organism per project, no matter the type
    private List<Organism> organism = new ArrayList<>();

    @Id
    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    @Column(length = 20)
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

    @Column(length = 1024)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Lob
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(mappedBy = "bioProject", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ProjectRelevance> getProjectRelevance() {
        return projectRelevance;
    }

    protected void setProjectRelevance(Set<ProjectRelevance> projectRelevance) {
        this.projectRelevance = projectRelevance;
    }

    public void addProjectRelevance(ProjectRelevance relevance) {
        relevance.setBioProject(this);
        this.getProjectRelevance().add(relevance);
    }

    @ElementCollection
    @Column(length = 32)
    public Set<String> getLocusTagPrefixes() {
        return locusTagPrefixes;
    }

    public void setLocusTagPrefixes(Set<String> locusTagPrefixes) {
        this.locusTagPrefixes = locusTagPrefixes;
    }

    @OneToMany(mappedBy = "bioProject", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Publication> getPublications() {
        return publications;
    }

    protected void setPublications(Set<Publication> publications) {
        this.publications = publications;
    }

    public void addPublication(Publication publication) {
        publication.setBioProject(this);
        this.getPublications().add(publication);
    }

    @OneToMany(mappedBy = "bioProject", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ExternalLink> getExternalLinks() {
        return externalLinks;
    }

    protected void setExternalLinks(Set<ExternalLink> externalLinks) {
        this.externalLinks = externalLinks;
    }

    public void addExternalLink(ExternalLink link) {
        link.setBioProject(this);
        getExternalLinks().add(link);
    }

    @OneToMany(mappedBy = "bioProject", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<DBXref> getCrossReferences() {
        return crossReferences;
    }

    protected void setCrossReferences(Set<DBXref> crossReferences) {
        this.crossReferences = crossReferences;
    }

    public void addDBXref(DBXref xref) {
        xref.setBioProject(this);
        this.getCrossReferences().add(xref);
    }

    @OneToMany(mappedBy = "bioProject", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<UserTerm> getUserTerms() {
        return userTerms;
    }

    protected void setUserTerms(Set<UserTerm> userTerms) {
        this.userTerms = userTerms;
    }

    public void addUserTerm(UserTerm term) {
        term.setBioProject(this);
        this.getUserTerms().add(term);
    }

    @OneToMany(mappedBy = "bioProject", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Grant> getGrants() {
        return grants;
    }

    protected void setGrants(Set<Grant> grants) {
        this.grants = grants;
    }

    public void addGrant(Grant grant) {
        grant.setBioProject(this);
        this.getGrants().add(grant);
    }

    /*
    Used a OneToMany here because orphan removal doesn't work with a OneToOne
    (or at least not the way you'd expect, making you set the property to null
    to get it to remove, which won't work if you're merging two objects).
    Organism doesn't use this approach for its own OneToOne relatiosn, but we
    fixup after loading with some queries that find orphans. They don't cause
    any problems, just take up space in the database. And possibly cause foreign
    key constraint violations.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bioProject", orphanRemoval = true)
    protected List<Organism> getOrganism() {
        return organism;
    }

    protected void setOrganism(List<Organism> organism) {
        this.organism = organism;
    }

    //@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    public Organism retrieveOrganism() {
        return this.getOrganism().isEmpty() ? null : organism.get(0);
    }

    public void updateOrganism(Organism organism) {
        List<Organism> list = new ArrayList<>();
        list.add(organism);
        organism.setBioProject(this);
        this.setOrganism(list);
    }

    // optional, but must be deleted if the project is deleted!
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bioProject")
    public Set<Mapping> getMappings() {
        return mappings;
    }

    public void setMappings(Set<Mapping> mappings) {
        this.mappings = mappings;
    }

}
