package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.bindings.*;
import net.straininfo2.grs.idloader.bioproject.domain.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static net.straininfo2.grs.idloader.bioproject.domain.ProjectRelevance.RelevantField.*;

/**
 * Translates XML binding class instances to the domain model of the database.
 * Not all information is included, only what is deemed necessary or
 * interesting. The documentation of the individual domain classes has some
 * more information on what is and isn't included.
 *
 * @see BioProject
 */
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

    public void addRelevanceFields(BioProject project, Project.ProjectDescr.Relevance relev) {
        if (relev == null) {
            return;
        }
        List<ProjectRelevance> relevances = new LinkedList<>();
        if (relev.getAgricultural() != null) {
            ProjectRelevance relevance = new ProjectRelevance(AGRICULTURAL, relev.getAgricultural());
            relevances.add(relevance);
        }
        if (relev.getEnvironmental() != null) {
            ProjectRelevance relevance = new ProjectRelevance(ENVIRONMENTAL, relev.getEnvironmental());
            relevances.add(relevance);
        }
        if (relev.getEvolution() != null) {
            ProjectRelevance relevance = new ProjectRelevance(EVOLUTION, relev.getEvolution());
            relevances.add(relevance);
        }
        if (relev.getIndustrial() != null) {
            ProjectRelevance relevance = new ProjectRelevance(INDUSTRIAL, relev.getIndustrial());
            relevances.add(relevance);
        }
        if (relev.getMedical() != null) {
            ProjectRelevance relevance = new ProjectRelevance(MEDICAL, relev.getMedical());
            relevances.add(relevance);
        }
        if (relev.getModelOrganism() != null) {
            ProjectRelevance relevance = new ProjectRelevance(MODEL_ORGANISM, relev.getModelOrganism());
            relevances.add(relevance);
        }
        if (relev.getOther() != null) {
            ProjectRelevance relevance = new ProjectRelevance(OTHER, relev.getOther());
            relevances.add(relevance);
        }
        project.setProjectRelevance(relevances);
    }

    public void addLocusTags(BioProject project, List<TypeLocusTagPrefix> prefixes) {
        if (prefixes == null) {
            return;
        }
        List<String> locusTags = new LinkedList<>();
        for (TypeLocusTagPrefix prefix : prefixes) {
            locusTags.add(prefix.getValue());
        }
        project.setLocusTagPrefixes(locusTags);
    }

    public void addPublications(BioProject project, List<TypePublication> publications) {
        ArrayList<Publication> pubList = new ArrayList<>(publications.size());
        for (TypePublication pub : publications) {
            Publication publication = new Publication();
            switch(pub.getDbType()) {
                case "ePMC":
                    publication.setDbType(Publication.PublicationDB.PMC);
                    break;
                case "ePubmed":
                    publication.setDbType(Publication.PublicationDB.PUBMED);
                    break;
                case "eDOI":
                    publication.setDbType(Publication.PublicationDB.DOI);
                    break;
                case "eNotAvailable":
                default:
                    publication.setDbType(Publication.PublicationDB.NOT_AVAILABLE);
                    break;
            }
            if (pub.getStatus() != null) {
                if (pub.getStatus().equals("ePublished")) {
                    publication.setPublicationStatus(Publication.PublicationStatus.PUBLISHED);
                }
                else {
                    publication.setPublicationStatus(Publication.PublicationStatus.UNPUBLISHED);
                }
            }
            publication.setPublicationDate(pub.getDate());
            publication.setPublicationId(pub.getId());
            publication.setFreeFormCitation(pub.getReference());
            TypePublication.StructuredCitation citation = pub.getStructuredCitation();
            if (citation != null) {
                publication.setTitle(citation.getTitle());
                TypePublication.StructuredCitation.Journal journal = citation.getJournal();
                publication.setJournalTitle(journal.getJournalTitle());
                publication.setIssue(journal.getIssue());
                publication.setPagesFrom(journal.getPagesFrom());
                publication.setPagesTo(journal.getPagesTo());
                publication.setVolume(journal.getVolume());
                publication.setYear(journal.getYear());
                if (citation.getAuthorSet() != null) {
                    List<Author> dAuthors = new ArrayList<>(citation.getAuthorSet().getAuthors().size());
                    for (TypePublication.StructuredCitation.AuthorSet.Author author : citation.getAuthorSet().getAuthors()) {
                        Author dAuthor = new Author();
                        dAuthor.setConsortium(author.getConsortium());
                        dAuthor.setFirstName(author.getName().getFirst());
                        dAuthor.setMiddleName(author.getName().getMiddle());
                        dAuthor.setLastName(author.getName().getLast());
                        dAuthor.setSuffix(author.getName().getSuffix());
                        dAuthors.add(dAuthor);
                    }
                    publication.setAuthors(dAuthors);
                }
            }
            pubList.add(publication);
        }
        project.setPublications(pubList);
    }

    public void addDescription(BioProject project, Project.ProjectDescr descr) {
        // TODO: links, refseq, relevance, userterm, locus tag prefix
        // TODO: grant information
        project.setDescription(descr.getDescription());
        project.setName(descr.getName());
        project.setTitle(descr.getTitle());
        addRelevanceFields(project, descr.getRelevance());
        addLocusTags(project, descr.getLocusTagPrefixes());
        addPublications(project, descr.getPublications());
    }

    public void addTypeSpecificInformation(BioProject project, Project.ProjectType type) {

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
