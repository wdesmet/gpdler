package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.bindings.Project;
import net.straininfo2.grs.idloader.bioproject.bindings.TypePackage;
import net.straininfo2.grs.idloader.bioproject.domain.BioProject;
import net.straininfo2.grs.idloader.bioproject.domain.MemoryBackedDomainHandler;
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
        // publications, external links, locus tag prefix and release date not parsed
        // "RefSeq" tag not used here
    }

}
