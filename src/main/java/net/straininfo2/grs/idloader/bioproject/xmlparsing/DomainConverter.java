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

            @Override
            public void processAdminBioProject(AdminBioProject project) {
            }

            @Override
            public void processSubmissionBioProject(SubmissionBioProject project) {
            }
        };
    }

    public DomainConverter(DomainHandler handler) {
        this.handler = handler;
    }

    public DomainHandler getHandler() {
        return handler;
    }

    public void setHandler(DomainHandler handler) {
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
        if (relev.getAgricultural() != null) {
            ProjectRelevance relevance = new ProjectRelevance(AGRICULTURAL, relev.getAgricultural());
            project.addProjectRelevance(relevance);
        }
        if (relev.getEnvironmental() != null) {
            ProjectRelevance relevance = new ProjectRelevance(ENVIRONMENTAL, relev.getEnvironmental());
            project.addProjectRelevance(relevance);
        }
        if (relev.getEvolution() != null) {
            ProjectRelevance relevance = new ProjectRelevance(EVOLUTION, relev.getEvolution());
            project.addProjectRelevance(relevance);
        }
        if (relev.getIndustrial() != null) {
            ProjectRelevance relevance = new ProjectRelevance(INDUSTRIAL, relev.getIndustrial());
            project.addProjectRelevance(relevance);
        }
        if (relev.getMedical() != null) {
            ProjectRelevance relevance = new ProjectRelevance(MEDICAL, relev.getMedical());
            project.addProjectRelevance(relevance);
        }
        if (relev.getModelOrganism() != null) {
            ProjectRelevance relevance = new ProjectRelevance(MODEL_ORGANISM, relev.getModelOrganism());
            project.addProjectRelevance(relevance);
        }
        if (relev.getOther() != null) {
            ProjectRelevance relevance = new ProjectRelevance(OTHER, relev.getOther());
            project.addProjectRelevance(relevance);
        }
    }

    public void addLocusTags(BioProject project, List<TypeLocusTagPrefix> prefixes) {
        if (prefixes == null) {
            return;
        }
        Set<String> locusTags = new HashSet<>();
        for (TypeLocusTagPrefix prefix : prefixes) {
            locusTags.add(prefix.getValue());
        }
        project.setLocusTagPrefixes(locusTags);
    }

    public void addPublications(BioProject project, List<TypePublication> publications) {
        for (TypePublication pub : publications) {
            Publication publication = new Publication();
            publication.setDbType(Publication.PublicationDB.valueOf(pub.getDbType()));

            if (pub.getStatus() != null) {
                publication.setPublicationStatus(Publication.PublicationStatus.valueOf(pub.getStatus()));
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
                    for (TypePublication.StructuredCitation.AuthorSet.Author author : citation.getAuthorSet().getAuthors()) {
                        Author dAuthor = new Author();
                        dAuthor.setConsortium(author.getConsortium());
                        dAuthor.setFirstName(author.getName().getFirst());
                        dAuthor.setMiddleName(author.getName().getMiddle());
                        dAuthor.setLastName(author.getName().getLast());
                        dAuthor.setSuffix(author.getName().getSuffix());
                        publication.addAuthor(dAuthor);
                    }
                }
            }
            project.addPublication(publication);
        }
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
        for (TypeExternalLink xmlLink : externalLinks) {
            if (xmlLink.getDbXREF() == null) {
                ExternalLink link = new ExternalLink();
                link.setCategory(xmlLink.getCategory());
                link.setLabel(xmlLink.getLabel());
                link.setUrl(xmlLink.getURL());
                project.addExternalLink(link);
            }
            else {
                DBXref ref = new DBXref();
                ref.setCategory(xmlLink.getCategory());
                ref.setLabel(xmlLink.getLabel());
                ref.setDb(xmlLink.getDbXREF().getDb());
                ref.setDbId(concatenateStringList(xmlLink.getDbXREF().getIDS(), ','));
                project.addDBXref(ref);
            }
        }
    }

    public void addUserTerms(BioProject project, List<Project.ProjectDescr.UserTerm> userTerms) {
        for (Project.ProjectDescr.UserTerm userTerm : userTerms) {
            UserTerm term = new UserTerm();
            term.setCategory(userTerm.getCategory());
            term.setTerm(userTerm.getTerm());
            term.setUnits(userTerm.getUnits());
            term.setValue(userTerm.getValue());
            project.addUserTerm(term);
        }
    }

    public void addGrants(BioProject project, List<Project.ProjectDescr.Grant> grants) {
        for (Project.ProjectDescr.Grant grant : grants) {
            Grant projectGrant = new Grant();
            projectGrant.setAgencyName(grant.getAgency().getValue());
            projectGrant.setAgencyAbbr(grant.getAgency().getAbbr());
            projectGrant.setGrantId(grant.getGrantId());
            projectGrant.setTitle(grant.getTitle());
            project.addGrant(projectGrant);
        }
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

    public void addBiologicalProperties(Organism organism, TypeOrganism.BiologicalProperties properties) {
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
                morphology.setGram(OrganismMorphology.Gram.valueOf(data.getGram()));
            }
            if (data.getMotility() != null) {
                morphology.setMotility(data.getMotility().equals("eYes"));
            }
            if (data.getShapes() != null) {
                Set<OrganismMorphology.Shape> shapes = EnumSet.noneOf(OrganismMorphology.Shape.class);
                for (String shape : data.getShapes()) {
                    shapes.add(OrganismMorphology.Shape.valueOf(shape));
                }
            }
            organism.setMorphology(morphology);
        }
        if (properties.getBiologicalSample() != null) {
            OrganismSample oSample = new OrganismSample();
            TypeOrganism.BiologicalProperties.BiologicalSample sample = properties.getBiologicalSample();
            if (sample.getCellSample() != null) {
                oSample.setIsolatedCell(sample.getCellSample().equals("eIsolated"));
            }
            if (sample.getTissueSample() != null) {
                oSample.setTissueSample(true);
            }
            else {
                oSample.setTissueSample(false);
            }
            if (sample.getCultureSample() != null) {
                oSample.setCultureSampleInfo(OrganismSample.CultureType.valueOf(sample.getCultureSample()));
            }
            organism.setSample(oSample);
        }
        if (properties.getEnvironment() != null) {
            OrganismEnvironment oEnv = new OrganismEnvironment();
            TypeOrganism.BiologicalProperties.Environment env = properties.getEnvironment();
            oEnv.setHabitat(env.getHabitat() == null ?
                    null :
                    OrganismEnvironment.Habitat.valueOf(env.getHabitat()));
            oEnv.setOxygenReq(env.getOxygenReq() == null ?
                    null :
                    OrganismEnvironment.OxygenReq.valueOf(env.getOxygenReq()));
            oEnv.setTemperatureRange(env.getTemperatureRange() == null ?
                    null :
                    OrganismEnvironment.TemperatureRange.valueOf(env.getTemperatureRange()));
            oEnv.setSalinity(env.getSalinity() == null ?
                    null :
                    OrganismEnvironment.Salinity.valueOf(env.getSalinity()));
            oEnv.setOptimumTemperature(env.getOptimumTemperature());
            organism.setEnvironment(oEnv);
        }
        if (properties.getPhenotype() != null) {
            OrganismPhenotype oPheno = new OrganismPhenotype();
            TypeOrganism.BiologicalProperties.Phenotype pheno = properties.getPhenotype();
            oPheno.setBioticRelationship(pheno.getBioticRelationship() == null ?
                    null :
                    OrganismPhenotype.BioticRelationship.valueOf(pheno.getBioticRelationship()));
            oPheno.setTrophicLevel(pheno.getTrophicLevel() == null ?
                    null :
                    OrganismPhenotype.TrophicLevel.valueOf(pheno.getTrophicLevel()));
            oPheno.setDisease(pheno.getDisease());
            organism.setPhenotype(oPheno);
        }
    }

    public void addOrganismData(BioProject project, TypeOrganism organismData) {
        if (organismData == null) {
            return;
        }
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
            addBiologicalProperties(organism, organismData.getBiologicalProperties());
        }
        project.updateOrganism(organism);
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

    public void addCommonFields(BioProject project, Project xmlProject) {
        addIdentifiers(project, xmlProject.getProjectID());
        addDescription(project, xmlProject.getProjectDescr());
        addTypeSpecificInformation(project, xmlProject.getProjectType());
    }

    @Override
    public void processPackage(TypePackage nextPackage) {
        Project xmlProject = nextPackage.getProject().getProject();
        //Submission xmlSubmission = nextPackage.getSubmission().getSubmission();
        if (xmlProject.getProjectType().getProjectTypeTopAdmin() != null) {
            AdminBioProject project = new AdminBioProject();
            if (xmlProject.getProjectType().getProjectTypeTopAdmin().getSubtype() != null) {
                project.setSubType(AdminBioProject.ProjectSubType.valueOf(xmlProject.getProjectType().getProjectTypeTopAdmin().getSubtype()));
                project.setDescriptionOther(xmlProject.getProjectType().getProjectTypeTopAdmin().getDescriptionSubtypeOther());
            }
            addCommonFields(project, xmlProject);
            handler.processAdminBioProject(project);
        }
        else if (xmlProject.getProjectType().getProjectTypeSubmission() != null) {
            SubmissionBioProject project = new SubmissionBioProject();
            addCommonFields(project, xmlProject);
            // TODO: add any relevant submission project fields
            handler.processSubmissionBioProject(project);
        }
        else {
            BioProject project = new BioProject();
            addCommonFields(project, xmlProject);
            handler.processBioProject(project);
        }
    }

    // TODO: test grant, user term, organism data

}
