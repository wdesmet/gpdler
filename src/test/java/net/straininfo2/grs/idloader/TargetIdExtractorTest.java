package net.straininfo2.grs.idloader;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TargetIdExtractorTest {

    private Map<Integer, List<Mapping>> constructGrs(String url1, String url2, Provider provider) {
        Map<Integer, List<Mapping>> grs = new HashMap<Integer, List<Mapping>>();
        List<Mapping> mappings = new ArrayList<Mapping>();
        mappings.add(new Mapping(url1, null, null, null, provider));
        grs.put(1, mappings);
        mappings = new ArrayList<Mapping>();
        mappings.add(new Mapping(url2, null, null, null, provider));
        grs.put(2, mappings);
        return grs;
    }

    @Test
    public void testVenterExtraction() {
        String url1 = "http://cmr.jcvi.org/cgi-bin/CMR/shared/GenomeProperties.cgi?states=all&amp;sub_org_val=ntyp01!";
        String url2 = "http://cmr.jcvi.org/cgi-bin/CMR/shared/GenomeProperties.cgi?states=all&amp;sub_org_val=gvc!";
        Provider provider = new Provider(
                "J. Craig Venter Institute - Genome Properties",
                "jcrventer",
                6982,
                "http://www.jcvi.org");
        Map<Integer, List<Mapping>> grs = constructGrs(url1, url2, provider);
        Map<Provider, TargetIdExtractor> extractors = Loader.constructExtractors(grs);
        assertEquals(extractors.size(), 1);
        assertEquals("ntyp01!", extractors.get(provider).extractTargetId(url1));
        assertEquals("gvc!", extractors.get(provider).extractTargetId(url2));
    }

    @Test
    public void testStraininfoExtraction() {
        String url1 = "http://www.straininfo.net/strains/110659";
        String url2 = "http://www.straininfo.net/strains/6328";
        Provider provider = new Provider("StrainInfo", "StrainInfo",
                6685, "http://www.straininfo.net");
        Map<Integer, List<Mapping>> grs = constructGrs(url1, url2, provider);
        Map<Provider, TargetIdExtractor> extractors = Loader.constructExtractors(grs);
        assertEquals("110659", extractors.get(provider).extractTargetId(url1));
        assertEquals("6328", extractors.get(provider).extractTargetId(url2));
    }

    @Test
    public void testHypotheticalAmpersand() {
        String u1 = "http://example.org/resource?q1=blah&q2=id1&q3=bleh";
        String u2 = "http://example.org/resource?q1=blah&q2=id2&q3=bleh";
        Provider p = new Provider("example", "e", 1, "http://example.org");
        Map<Integer, List<Mapping>> grs = constructGrs(u1, u2, p);
        Map<Provider, TargetIdExtractor> extractorMap = Loader.constructExtractors(grs);
        assertEquals("Extractor did not extract correct part", "id1", extractorMap.get(p).extractTargetId(u1));
    }

    @Test
    public void testDummyReturnsNull() {
        assertTrue(new TargetIdExtractor(-1).extractTargetId("http://www.straininfo.net/") == null);
    }
}