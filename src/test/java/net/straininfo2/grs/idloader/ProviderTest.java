package net.straininfo2.grs.idloader;

import net.straininfo2.grs.idloader.bioproject.domain.mappings.Provider;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProviderTest {

    private Provider provider;

    @Before
    public void setup() {
        this.provider = new Provider("StrainInfo", "StrainInfo", 6685, "http://www.straininfo.net");
    }
    @Test
    public void testOutput() {
        assertEquals("Provider: StrainInfo(StrainInfo) - id 6685", provider.toString());
    }

    @Test
    public void testEquals() {
        ConcurrentHashMap<Provider, Provider> map = new ConcurrentHashMap<Provider, Provider>();
        map.put(provider, provider);
        Provider next = new Provider(provider.getName(), provider.getAbbr(), provider.getId(), provider.getUrl());
        assertTrue("hash map added the provider, even though it shouldn't!",
                map.putIfAbsent(next, next) != null);
        assertTrue(provider.equals(provider));
    }

    @Test
    public void testNotEquals() {
        Provider c1 = new Provider("Test", "T", 2819, null);
        Provider c2 = new Provider(null, null, 1, null);
        assertTrue(!(provider.equals(c1) || provider.equals(c2)));
    }

}
