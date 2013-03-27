package net.straininfo2.grs.idloader;

import net.straininfo2.grs.idloader.bioproject.domain.mappings.Category;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertTrue;

public class CategoryTest {

    private ConcurrentHashMap<Category, Category> categories;

    private Category original;

    private final static String DEFAULT="test";

    @Before
    public void setup() {
        this.categories = new ConcurrentHashMap<>();
        this.original = new Category(DEFAULT);
        categories.put(original, original);
    }

    @Test
    public void checkDuplicates() {
        Category newCat = new Category(DEFAULT);
        assertTrue("Same string gave different objects",
                this.categories.putIfAbsent(newCat, newCat) == original);
    }

    @Test
    public void equalsShouldBeTrue() {
        assertTrue("Categories did not match.",
                original.equals(new Category(DEFAULT)));
    }

    @Test
    public void caseDiffersEqualsShouldBeFalse() {
        Category second = new Category(DEFAULT.toUpperCase());
        assertTrue("Categories did not match.",
                !original.equals(categories.putIfAbsent(second, second)));
    }

    @Test
    public void equalsShouldBeFalse() {
        assertTrue("Categories matched even though they have different names.",
                !original.equals(new Category("Blah")));
    }

    @Test
    public void testEqualsShortcut() {
        assertTrue("Original didn't match itself!", original.equals(original));
    }

    @Test
    public void testName() {
        assertTrue(original.toString().equals(DEFAULT));
    }

    @Test
    public void testHashCode() {
        assertTrue(original.hashCode() == DEFAULT.hashCode());
    }
}
