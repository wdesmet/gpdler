package net.straininfo2.grs.idloader.bioproject.eutils;

import net.straininfo2.grs.idloader.Loader;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class EutilsDownloaderTest {

    @Test
    public void generalAddIntsTest() {
        // added because I did it entirely wrong, an array with contents
        // [3] would cause IndexOutOfBoundsException
        int[] ar = { 3 };
        List<Integer> l = new LinkedList<Integer>();
        EutilsDownloader.addInts(l, ar);
        assertTrue("List contains wrong or wrong amount of integers", l.size() == 1 && l.get(0) == ar[0]);
    }

    @Test
    public void addIntsFromEmptyArray() {
        int [] ar = {};
        List<Integer> l = new LinkedList<Integer>();
        EutilsDownloader.addInts(l, ar);
        assertTrue("List should be empty", l.isEmpty());
    }

}
