package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.bindings.Project;
import net.straininfo2.grs.idloader.bioproject.bindings.TypePackage;
import net.straininfo2.grs.idloader.bioproject.domain.BioProject;
import net.straininfo2.grs.idloader.bioproject.domain.ProjectRelevance;
import net.straininfo2.grs.idloader.bioproject.domain.Publication;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static net.straininfo2.grs.idloader.bioproject.domain.Archive.NCBI;

/**
 * Tests for various aspects of the conversion to domain objects.
 */
public class DomainConversionTest {

    private static Project retrieveBorreliaProject() throws Exception {
        List<TypePackage> typePackages = DocumentChunkerTest.parseBioProjectFile();
        // SAX parses in document order, so we know which one this is
        return typePackages.get(0).getProject().getProject();
    }

    private static Project retrieveBordetellaProject() throws Exception {
        List<TypePackage> typePackages = DocumentChunkerTest.parseBioProjectFile();
        return typePackages.get(1).getProject().getProject();
    }

    private static Project retrieveAtribeusProject() throws Exception {
        List<TypePackage> typePackages = DocumentChunkerTest.parseBioProjectFile();
        return typePackages.get(2).getProject().getProject();
    }

    @Test
    public void testIdentifierConversion() throws Exception {
        DomainConverter converter = new DomainConverter();
        BioProject project = new BioProject();
        converter.addIdentifiers(project, retrieveBorreliaProject().getProjectID());
        assertEquals("PRJNA3", project.getAccession());
        assertEquals(3, project.getProjectId());
        assertEquals(NCBI, project.getArchive());
    }

    @Test
    public void testDescriptionConversion() throws Exception {
        DomainConverter converter = new DomainConverter();
        BioProject project = new BioProject();
        converter.addDescription(project, retrieveBorreliaProject().getProjectDescr());
        assertEquals("Borrelia burgdorferi B31", project.getName());
        assertEquals("Causes Lyme disease", project.getTitle());
        assertTrue(project.getDescription().contains("ATCC 35210"));
        // external links, locus tag prefix and release date not parsed
        // "RefSeq" tag not used here
    }

    @Test
    public void testRelevanceConversion() throws Exception {
        BioProject project = new BioProject();
        DomainConverter converter = new DomainConverter();
        converter.addRelevanceFields(project, retrieveBordetellaProject().getProjectDescr().getRelevance());
        assertTrue(project.getProjectRelevance() != null);
        assertEquals(1, project.getProjectRelevance().size());
        assertEquals(ProjectRelevance.RelevantField.MEDICAL, project.getProjectRelevance().iterator().next().getRelevantField());
    }

    @Test
    public void testLocusTags() throws Exception {
        BioProject project = new BioProject();
        new DomainConverter().addLocusTags(project, retrieveBorreliaProject().getProjectDescr().getLocusTagPrefixes());
        assertEquals("BB", project.getLocusTagPrefixes().iterator().next());
    }

    @Test
    public void testPublications() throws Exception {
        BioProject project = new BioProject();
        new DomainConverter().addPublications(project, retrieveAtribeusProject().getProjectDescr().getPublications());
        Publication publication = project.getPublications().iterator().next();
        assertEquals("PLoS One.", publication.getJournalTitle());
        assertEquals("Schountz" ,publication.getAuthors().get(0).getLastName());
    }

}
