package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.bindings.*;
import net.straininfo2.grs.idloader.bioproject.domain.*;

import java.util.*;

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

    /*
    Note; this class is long, but the code is all very simple loading of one
    representation into the other. The end result is a domain class layout
    that has no dependencies at all on the XML representation. Obviously some
    of this could be moved to static constructors on the domain objects, but
    not much is gained from that, aside from a shorter and less procedural
    looking class.
     */

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

    // Helper functions.

    /**
     * Concatenates all strings in the given list, separating them with the
     * supplied character.
     *
     * @param strings List of strings to concatenate
     * @param concatChar Character to separate concatenated strings with
     * @return Single strings containing all members of the supplied list,
     * separated by concatChar
     */
    public static String concatenateStringList(List<String> strings, char concatChar) {
        Iterator<String> iter = strings.iterator();
        StringBuilder sb = new StringBuilder();
        while (iter.hasNext()) {
            sb.append(iter.next());
            if (iter.hasNext()) {
                sb.append(concatChar);
            }
        }
        return sb.toString();
    }

    // object construction functions

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

    /**
     * Adds external links. There are actually two types: normal URLs and
     * DBXref links. They appear to be the same type because of the way the
     * XML is structured, but it's best to separate them here.
     *
     * @param project Project to add the external links to
     * @param externalLinks List of external links parsed from XML
     */
    public void addLinks(BioProject project, List<TypeExternalLink> externalLinks) {
        List<ExternalLink> links = new LinkedList<>();
        List<DBXref> crossReferences = new LinkedList<>();
        for (TypeExternalLink xmlLink : externalLinks) {
            if (xmlLink.getDbXREF() == null) {
                ExternalLink link = new ExternalLink();
                link.setCategory(xmlLink.getCategory());
                link.setLabel(xmlLink.getLabel());
                link.setUrl(xmlLink.getURL());
                links.add(link);
            }
            else {
                DBXref ref = new DBXref();
                ref.setCategory(xmlLink.getCategory());
                ref.setLabel(xmlLink.getLabel());
                ref.setDb(xmlLink.getDbXREF().getDb());
                ref.setId(concatenateStringList(xmlLink.getDbXREF().getIDS(), ','));
                crossReferences.add(ref);
            }
        }
        project.setExternalLinks(links);
        project.setCrossReferences(crossReferences);
    }

    public void addUserTerms(BioProject project, List<Project.ProjectDescr.UserTerm> userTerms) {
        List<UserTerm> terms = new ArrayList<>(userTerms.size());
        for (Project.ProjectDescr.UserTerm userTerm : userTerms) {
            UserTerm term = new UserTerm();
            term.setCategory(userTerm.getCategory());
            term.setTerm(userTerm.getTerm());
            term.setUnits(userTerm.getUnits());
            term.setValue(userTerm.getValue());
            terms.add(term);
        }
        project.setUserTerms(terms);
    }

    public void addGrants(BioProject project, List<Project.ProjectDescr.Grant> grants) {
        List<Grant> projectGrants = new ArrayList<>(grants.size());
        for (Project.ProjectDescr.Grant grant : grants) {
            Grant projectGrant = new Grant();
            projectGrant.setAgencyName(grant.getAgency().getValue());
            projectGrant.setAgencyAbbr(grant.getAgency().getAbbr());
            projectGrant.setGrantId(grant.getGrantId());
            projectGrant.setTitle(grant.getTitle());
            projectGrants.add(projectGrant);
        }
        project.setGrants(projectGrants);
    }

    public void addDescription(BioProject project, Project.ProjectDescr descr) {
        project.setDescription(descr.getDescription());
        project.setName(descr.getName());
        project.setTitle(descr.getTitle());
        addRelevanceFields(project, descr.getRelevance());
        addLocusTags(project, descr.getLocusTagPrefixes());
        addPublications(project, descr.getPublications());
        addLinks(project, descr.getExternalLinks());
        addUserTerms(project, descr.getUserTerms());
        addGrants(project, descr.getGrants());
        // not mapped: RefSeq
    }

    public void addBiologicalProperties(BioProject project, TypeOrganism.BiologicalProperties properties) {
        if (properties.getMorphology() != null) {
            TypeOrganism.BiologicalProperties.Morphology data = properties.getMorphology();
            OrganismMorphology morphology = new OrganismMorphology();
            if (data.getEndospores() != null) {
                morphology.setEndospores(data.getEndospores().equals("eYes"));
            }
            if (data.getEnveloped() != null) {
                morphology.setEnveloped(data.getEnveloped().equals("eYes"));
            }
            if (data.getGram() != null) {
                morphology.setGram(OrganismMorphology.Gram.fromString(data.getGram()));
            }
            if (data.getMotility() != null) {
                morphology.setMotility(data.getMotility().equals("eYes"));
            }
            if (data.getShapes() != null) {
                Set<OrganismMorphology.Shape> shapes = EnumSet.noneOf(OrganismMorphology.Shape.class);
                for (String shape : data.getShapes()) {
                    shapes.add(OrganismMorphology.Shape.fromString(shape));
                }
            }
        }
        if (properties.getBiologicalSample() != null) {
            // TODO: add sample data
        }
        if (properties.getEnvironment() != null) {
            // TODO: add environment data
        }
        if (properties.getPhenotype() != null) {
            // TODO: add phenotype
        }
    }

    public void addOrganismData(BioProject project, TypeOrganism organismData) {
        Organism organism = new Organism();
        organism.setOrganismName(organismData.getOrganismName());
        organism.setLabel(organismData.getLabel());
        organism.setStrain(organismData.getStrain());
        organism.setIsolateName(organismData.getIsolateName());
        organism.setBreed(organismData.getBreed());
        organism.setCultivar(organismData.getCultivar());
        organism.setSupergroup(organismData.getSupergroup());
        organism.setTaxID(organismData.getTaxID());
        organism.setSpecies(organismData.getSpecies());
        organism.setOrganization(organismData.getOrganization());
        organism.setReproduction(organismData.getReproduction());
        if (organismData.getGenomeSize() != null) {
            organism.setGenomeSize(organismData.getGenomeSize().getValue().longValue());
            organism.setGenomeSizeUnits(organismData.getGenomeSize().getUnits());
        }
        if (organismData.getBiologicalProperties() != null) {
            addBiologicalProperties(project, organismData.getBiologicalProperties());
        }
        project.setOrganism(organism);
    }

    public void addTypeSpecificInformation(BioProject project, Project.ProjectType type) {
        if (type.getProjectTypeSubmission() != null) {
            addOrganismData(project, type.getProjectTypeSubmission().getTarget().getOrganism());
        }
        else if (type.getProjectTypeTopAdmin() != null) {
            addOrganismData(project, type.getProjectTypeTopAdmin().getOrganism());
        }
        else if (type.getProjectTypeTopSingleOrganism() != null) {
            addOrganismData(project, type.getProjectTypeTopSingleOrganism().getOrganism());
        }
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

    // TODO: test grant, user term

}
