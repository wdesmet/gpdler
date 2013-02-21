package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.bindings.TypePackage;
import net.straininfo2.grs.idloader.bioproject.domain.BioProject;
import net.straininfo2.grs.idloader.bioproject.domain.MemoryBackedDomainHandler;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static net.straininfo2.grs.idloader.bioproject.domain.Archive.NCBI;

/**
 * Tests for various aspects of the conversion to domain objects.
 */
public class DomainConversionTest {

    @Test
    public void testIdentifierConversion() throws Exception {
        List<TypePackage> typePackages = DocumentChunkerTest.parseBioProjectFile();
        MemoryBackedDomainHandler handler = new MemoryBackedDomainHandler();
        DomainConverter converter = new DomainConverter();
        // SAX parses in document order, so we know which one this is
        BioProject project = new BioProject();
        converter.addIdentifiers(project, typePackages.get(0).getProject().getProject().getProjectID());
        assertEquals("PRJNA3", project.getAccession());
        assertEquals(3, project.getProjectId());
        assertEquals(NCBI, project.getArchive());
    }

}
