package net.straininfo2.grs.idloader.bioproject.eutils;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EntrezSearchResultTest {
    /**
     * The EntrezSearchResult class expects to know how many IDs will be added
     * before the user does so.
     */
    @Test(expected = RuntimeException.class)
    public void testSearchResultAddingIdsPrematurely() {
        EntrezSearchResult res = new EntrezSearchResult();
        res.addId(5);
    }

    @Test
    public void testSearchResultCountAndStartGiven() {
        EntrezSearchResult res = new EntrezSearchResult();
        res.setCount(1);
        res.setRetStart(0);
        res.setRetMax(100);
        res.addId(555);
        assertTrue(res.getIds().length == 1 && res.getIds()[0] == 555);
    }

    @Test(expected = RuntimeException.class)
    public void addTooManyIdsToSearchResult() {
        EntrezSearchResult res = new EntrezSearchResult();
        res.setCount(1);
        res.setRetStart(0);
        res.setRetMax(1);
        res.addId(0);
        res.addId(1);
    }
}
