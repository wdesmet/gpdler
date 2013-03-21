package net.straininfo2.grs.idloader.bioproject.eutils;

import net.straininfo2.grs.idloader.bioproject.domain.mappings.Category;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Mapping;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Provider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MappingTest {

    final static String[] MAPPING_FILES = new String[] { "4.xml","13.xml","15.xml","16.xml","17.xml","18.xml","19.xml","20.xml","21.xml","22.xml","24.xml","25.xml","26.xml","27.xml","31.xml","36.xml","38.xml","42.xml","78.xml","79.xml","306.xml","351.xml","12526.xml","12997.xml","13213.xml","13595.xml","16689.xml","17587.xml","28295.xml","29437.xml","31487.xml","41699.xml","42379.xml","56077.xml","62447.xml","64923.xml","67189.xml","79219.xml","89265.xml","164841.xml" };

    // read-only!
    private static Map<Integer, List<Mapping>> mappings;

    private final Mapping testMapping = new Mapping("http://genomesonline.org/cgi-bin/GOLD/bin/GOLDCards.cgi?goldstamp=Gc00174", "organism-specific",
            "GOLDCARD: Gc00174", new Category("Molecular Biology Databases"), new Provider("Genomes On Line Database", "GOLD", 7663, "http://genomesonline.org"));

    @BeforeClass
    public static void setup() throws XMLStreamException {
        mappings = createMappingList(MAPPING_FILES[0]);
    }

    public static Map<Integer,List<Mapping>> createMappingList(String filename) throws XMLStreamException {
        // utility function that performs the parsing on a list of mappings already available
        EutilsXmlParser parser = new EutilsXmlParser();
        return parser.parseMappings(MappingTest.class.getClassLoader().getResourceAsStream("linkoutfiles/" + filename));
    }

    @Test
    public void testMappingParsing() throws XMLStreamException {
        // calling our helper function on one of the files should provide a good test of parsing
        Assert.assertTrue("Mappings list has wrong nr of items", mappings.get(4).size() == 6);
    }

    @Test
    public void testEquals() {
        Assert.assertTrue(mappings.get(4).contains(mappings.get(4).get(3)));
    }

    @Test
    public void testProviderParsing() {
        Mapping m = mappings.get(4).get(0);
        assertTrue(m.getProvider() != null);
    }

    @Test
    public void testCategoryParsing() {
        Mapping m = mappings.get(4).get(0);
        assertTrue(m.getCategory() != null);
    }

    @Test
    public void testIndividualParsing() throws XMLStreamException {
        // chooses an example and constructs it manually for comparison

        assertTrue("Couldn't find " + testMapping.toString() + " in the mappings",
                mappings.get(4).contains(testMapping));
    }

    @Test
    public void testEqualsWithNulls() {
        Mapping m = new Mapping("http://genomesonline.org/cgi-bin/GOLD/bin/GOLDCards.cgi?goldstamp=Gc00174", "organism-specific", "GOLDCARD: Gc00174", null, null);
        m.equals(mappings.get(4).get(5));
    }

    @Test
    public void testToString() {
        Mapping m = mappings.get(4).get(0);
        assertEquals(m.getLinkName(), m.toString());
    }

    @Test
    public void checkHashCode() {
        for (Mapping m : mappings.get(4)) {
            assertTrue(!(m.equals(testMapping) && m.hashCode() != testMapping.hashCode()));
            assertTrue(!(!m.equals(testMapping) && m.hashCode() == testMapping.hashCode())); // not necessary for a hash code, but should be true for a lot of elements
        }
    }

    @Test
    public void testProviderList() {
        assertTrue(Mapping.listProviders(mappings.get(4)).size() == 5);
    }

    @Test
    public void testMappingsComparison() {
        List<Mapping> secondList = new ArrayList<Mapping>(mappings.get(4).size());
        List<Mapping> revCollection = new ArrayList<Mapping>(mappings.get(4));
        Collections.reverse(revCollection);
        for (Mapping m : revCollection) {
            secondList.add(m);
            if (secondList.size() < mappings.get(4).size()) {
                assertTrue(Mapping.differentMapping(mappings.get(4), secondList));
            }
            else{
                assertTrue(!Mapping.differentMapping(mappings.get(4), secondList));
            }
        }
    }

    @Test(expected = AssertionError.class)
    public void checkConstructorAssert() {
        Mapping m = new Mapping(null, null, null, null, null);
    }
}
