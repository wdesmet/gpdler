package net.straininfo2.grs.idloader.bioproject.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the enums in OrganismMorphology.
 */
public class MorphologyTest {

    @Test
    public void testShapeToString() {
        assertEquals("eBacilli", OrganismMorphology.Shape.eBacilli.toString());
        assertEquals("eCurvedShaped", OrganismMorphology.Shape.eCurvedShaped.toString());
    }

    @Test
    public void testStringToShape() {
        assertEquals(OrganismMorphology.Shape.eFusobacteria, OrganismMorphology.Shape.valueOf("eFusobacteria"));
    }
}
