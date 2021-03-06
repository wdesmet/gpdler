package net.straininfo2.grs.idloader;

import net.straininfo2.grs.idloader.bioproject.domain.mappings.Mapping;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Provider;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TargetIdExtractorTest {

    private Map<Integer, List<Mapping>> constructGrs(String url1, String url2, Provider provider) {
        Map<Integer, List<Mapping>> grs = new HashMap<>();
        List<Mapping> mappings = new ArrayList<>();
        mappings.add(new Mapping(url1, null, null, null, provider));
        grs.put(1, mappings);
        mappings = new ArrayList<>();
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
    public void testSilvaExtraction() {
        String url1 = "http://www.arb-silva.de/search/show/ssu/insdc/13456";
        String url2 = "http://www.arb-silva.de/search/show/ssu/insdc/13457";
        String url3 = "http://www.arb-silva.de/search/show/lsu/insdc/13456";
        Provider provider = new Provider(
                "SILVA", "SILVA", 1, "http://www.arb-silva.de/");
        Map<Integer, List<Mapping>> grs = constructGrs(url1, url2, provider);
        Map<Provider, TargetIdExtractor> extractors = Loader.constructExtractors(grs);
        assertEquals("13456", extractors.get(provider).extractTargetId(url1));
        assertEquals("13457", extractors.get(provider).extractTargetId(url2));
        assertEquals("13456", extractors.get(provider).extractTargetId(url3));
        grs = constructGrs(url1, url3, provider);
        extractors = Loader.constructExtractors(grs);
        assertEquals("13456", extractors.get(provider).extractTargetId(url1));
        assertEquals("13457", extractors.get(provider).extractTargetId(url2));
        assertEquals("13456", extractors.get(provider).extractTargetId(url3));
    }

    @Test
    public void testGoldExtraction() {
        String url1 = "http://genomesonline.org/cgi-bin/GOLD/bin/GOLDCards.cgi?goldstamp=Gi05080";
        String url2 = "http://genomesonline.org/cgi-bin/GOLD/bin/GOLDCards.cgi?goldstamp=Gi05232";
        Provider provider = new Provider(
                "GOLD", "GOLD", 1, "http://genomesonline.org/");
        Map<Integer, List<Mapping>> grs = constructGrs(url1, url2, provider);
        Map<Provider, TargetIdExtractor> extractors = Loader.constructExtractors(grs);
        assertEquals("Gi05080", extractors.get(provider).extractTargetId(url1));
        assertEquals("Gi05232", extractors.get(provider).extractTargetId(url2));
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
